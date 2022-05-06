package fr.inria.diverse.api.client;

import fr.inria.diverse.config.FileConfig;
import fr.inria.diverse.model.RawRepository;
import fr.inria.diverse.model.exception.BusinessCheckedException;
import fr.inria.diverse.model.exception.ErrorCode;
import fr.inria.diverse.model.graphql.*;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.graphql.client.Response;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static fr.inria.diverse.Utils.save;

@ApplicationScoped
public class GithubGraphQlEndpoint {
    private DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
    private ZonedDateTime startDate = LocalDateTime.of(2013, 8, 1, 0, 0).atZone(ZoneId.of("Europe/Paris"));

    @Inject
    private ReadmeCriteria readmeCriteria;
    @Inject
    private DescriptionCriteria decriptionCriteria;
    @Inject
    @GraphQLClient("github-graphql")
    DynamicGraphQLClient githubClient;
    @Inject
    FileConfig config;

    public List<RawRepository> getAllRawRepositoriesHavingGplayLinkInReadme(boolean checkpoint){
        return this.getAllRepositories(this.readmeCriteria,checkpoint);
    }

    public List<RawRepository> getAllRawRepositoriesHavingGplayLinkInDescription(boolean checkpoint){
        return this.getAllRepositories(this.decriptionCriteria,checkpoint);
    }
    /**
     * Query Github GraphQL API to find repositories that contain a link to a google play application
     * It can search either on readme or the description
     *
     * @param criteria wether it should search on the readme else in description
     * @return the corresponding list of GithubGraphQLRepository
     */
    private List<RawRepository> getAllRepositories(Criteria criteria, boolean checkpoint) {
        System.out.println("Getting all repo containing a google play uri in " + criteria.getCriteriaName());
        List<GithubGraphQLRepository> ghRepos = new LinkedList<>();
        //Due to Github limitation we need to decompose our requests to request returning less than 1k results. Then we can
        //query the api and use the pagination mechanism to query 100 response by page (the maximum).
        //In order to reduce the number of response we use creation date to decompose by batch of 1k query
        String currentCreatedPattern = "<" + this.formater.format(startDate);
        ZonedDateTime currentIntervalStart = null;
        ZonedDateTime currentIntervalEnd = startDate;
        ZonedDateTime today = ZonedDateTime.now();

        //We choose to express increment in minutes because some day granularity is too big in some cases ex : 2017-10-26
        //The time interval should be adaptable, in the following code the intervale is divided by 2 each time an error is
        //thrown
        int incrementInMinutes = criteria.getDefaultIncrementInMinutes();
        //Todo : find a way to do it in a parrallel way (divide by time interval)
        do {
            //If not in first iteration
            if (currentIntervalStart != null) {
                currentCreatedPattern = this.getPattern(currentIntervalStart, currentIntervalEnd);
            }
            System.out.println("----Start :" + currentIntervalStart + " End :" + currentIntervalEnd + "----");
            try {
                List<GithubGraphQLRepository> currentRes = searchOnInterval(currentCreatedPattern, criteria);
                ghRepos.addAll(currentRes);
            } catch (BusinessCheckedException e) {
                if (e.getCode().equals(ErrorCode.RESULTS_EXCEED_1000)) {
                    System.out.println("Error, RESULTS EXCEED 1000");
                    //decrease the increment and revert interval
                    if (currentIntervalStart == null) {
                        throw new RuntimeException("Case not managed, your default increment is really too high");
                    }
                    incrementInMinutes = incrementInMinutes / 2;
                    System.out.println("new increment : " + incrementInMinutes);
                    currentIntervalEnd = currentIntervalStart.plusMinutes(incrementInMinutes);
                    continue;
                }
            }
            incrementInMinutes = criteria.getDefaultIncrementInMinutes();
            System.out.println("Res Size :" + ghRepos.size());
            //For next iteration
            //If not in first iteration
            if (currentIntervalStart != null) {
                currentIntervalStart = currentIntervalEnd;
            } else {
                currentIntervalStart = startDate;
            }
            currentIntervalEnd = currentIntervalEnd.plusMinutes(incrementInMinutes);

        } while (today.compareTo(ChronoZonedDateTime.from(currentIntervalStart)) > 0);
        if (checkpoint) {
            String checkpointUri = criteria.getRawCheckpointUri();
            System.out.println("Saving raw repo containing gplay uri in" + criteria.getCriteriaName());
            save(checkpointUri, ghRepos);
        }
        List<RawRepository> res = ghRepos.parallelStream().map(repo -> criteria.getRawRepositoryFromGhRepo(repo))
                .collect(Collectors.toList());
        System.out.println("Found "+res.size()+" different result searching by "+criteria.getCriteriaName()+" criteria");

        return res;
    }

    /**
     * Search for GithubGraphQLRepositories on a given interval describe on the "createdPattern" this pattern follow the
     * github search api and the "created" field to filter by repo creation date.
     * The Query must return less than 1k results else github api does not work,  a BusinessCheckedException can be
     * thrown
     *
     * @param createdPattern the pattern to filter by date
     * @param criteria       wether we search in readme or in description
     * @return The list of repositories
     * @throws BusinessCheckedException RESULTS_EXCEED_1000 error when the query produce more than 1k repos (github api
     *                                  limitation)
     */
    private List<GithubGraphQLRepository> searchOnInterval(String createdPattern, Criteria criteria) throws BusinessCheckedException {
        GithubGraphQLResponse last;
        List<GithubGraphQLRepository> res = new LinkedList<>();
        String cursor = null;
        int i = 0;
        do {
            last = this.searchRepositoryQuery(cursor, createdPattern, criteria);
            if (last.getRepositoryCount() > 1000) {
                //Throw error to force caller to change "createdPattern" instead of getting the first 1000 elements
                //and then notify the date of the last element. We cannot do that because it seems that github api do not
                //order very well big query .. so using such mechanism can induce a lost of repo.. so I prefer to throw
                //an error !
                throw new BusinessCheckedException(ErrorCode.RESULTS_EXCEED_1000);
            }
            cursor = last.getPageInfo().getEndCursor();
            res.addAll(last.getEdges().stream().map(edge -> edge.getRepository()).collect(Collectors.toList()));
            System.out.println("Request:" + i + " over " + last.getRepositoryCount() / 100 + " (found " + last.getRepositoryCount() + ")");
            i++;
        } while (last.getPageInfo().isHasNextPage());
        return res;
    }

    /**
     * Search repository query
     *
     * @param after    cursor id
     * @param created  created pattern
     * @param criteria wheter it search in readme else in description
     * @return A GithubGraphQLResponse containing the first 100 repos
     */
    private GithubGraphQLResponse searchRepositoryQuery(String after, String created, Criteria criteria) {
        String query = "query SearchRepo($queryString: String!, $after: String) { " + "search(query: $queryString, type: REPOSITORY, first: 100, after: $after) { " + "repositoryCount " + "pageInfo{" + "endCursor " + "hasNextPage " + "} " + "edges { " + "cursor " + "repository: node { " + "... on Repository { " + "readme: object(expression: \"master:README.md\") { " + "... on Blob { " + "text " + " } " + " } " + "url " + "sshUrl " + "isFork " + "name " + "descriptionHTML " + "stars:stargazers { " + "totalCount " + "}" + "}" + "}" + "}" + "}" + "}";
        Map<String, Object> variables = new HashMap<>(); // <3>
        variables.put("queryString", criteria.getGraphQlQueryArg(created));
        variables.put("after", after);
        try {
            Response response = githubClient.executeSync(query, variables);
            return response.getObject(GithubGraphQLResponse.class, "search");
        } catch (ExecutionException e) {
            throw new RuntimeException("Error while getting repos", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error while getting repos", e);
        }
    }

    private String getPattern(ZonedDateTime start, ZonedDateTime end) {
        return this.formater.format(start) + ".." + this.formater.format(end);
    }








}
