package fr.inria.diverse;

import fr.inria.diverse.model.exception.BusinessCheckedException;
import fr.inria.diverse.model.exception.ErrorCode;
import fr.inria.diverse.model.graphql.GithubGraphQLRepository;
import fr.inria.diverse.model.graphql.GithubGraphQLResponse;
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

@ApplicationScoped
public class GithubGraphQlEndpoint {
    private  DateTimeFormatter formater =DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
    private ZonedDateTime startDate =  LocalDateTime.of(2013,8,1,0,0).atZone(ZoneId.of( "Europe/Paris" ));
    //We choose to express increment in minutes because some day granularity is too big in some cases ex : 2017-10-26
    private int defaultIncrementInMinutes = 60*24*60;
    @Inject
    @GraphQLClient("github-graphql")
    DynamicGraphQLClient githubClient;


    public List<GithubGraphQLRepository> getAllRepositories(boolean searchInReadme) {
        System.out.println("Getting all repo containing a google play uri in "+(searchInReadme?"readme":"description"));
        List<GithubGraphQLRepository> res = new LinkedList<>();

        String currentCreatedPattern = "<"+this.formater.format(startDate);
        ZonedDateTime currentIntervalStart = null;
        ZonedDateTime currentIntervalEnd= startDate;
        ZonedDateTime today = ZonedDateTime.now() ;
        int incrementInMinutes = defaultIncrementInMinutes;

        do {
            //If not in first iteration
            if(currentIntervalStart!=null){
                currentCreatedPattern = this.getPattern(currentIntervalStart,currentIntervalEnd);
            }
            System.out.println("----Start :"+currentIntervalStart+" End :"+currentIntervalEnd+"----");
            try {
                List<GithubGraphQLRepository> currentRes= searchOnInterval(currentCreatedPattern,searchInReadme);
                res.addAll(currentRes);
            } catch (BusinessCheckedException e) {
                if (e.getCode().equals(ErrorCode.RESULTS_EXCEED_1000)) {
                    System.out.println("Error, RESULTS EXCEED 1000");
                    //decrease the increment and revert interval
                    if(currentIntervalStart == null){
                        throw new RuntimeException("Case not managed, your default increment is really too high");
                    }
                    incrementInMinutes = incrementInMinutes / 2;
                    System.out.println("new increment : "+incrementInMinutes);
                    currentIntervalEnd=currentIntervalStart.plusMinutes(incrementInMinutes);
                    continue;
                }
            }
            incrementInMinutes= defaultIncrementInMinutes;
            System.out.println("Res Size :" + res.size());
            //For next iteration
            //If not in first iteration
            if(currentIntervalStart!=null){
                currentIntervalStart=currentIntervalEnd;
            }else{
                currentIntervalStart=startDate;
            }
            currentIntervalEnd=currentIntervalEnd.plusMinutes(incrementInMinutes);

        }while(today.compareTo(ChronoZonedDateTime.from(currentIntervalStart))>0);
        return res;
    }

    public List<GithubGraphQLRepository> searchOnInterval(String createdPattern,boolean searchInReadme) throws BusinessCheckedException {
        GithubGraphQLResponse last;
        List<GithubGraphQLRepository> res = new LinkedList<>();
        String cursor=null;
        int i=1;
        do {
            last = this.searchRepositoryQuery(cursor, createdPattern,searchInReadme);
            if(last.getRepositoryCount()>1000){
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
     * @param after cursor id
     * @param created created pattern
     * @param searchInReadme wheter it search in readme else in description
     * @return A GithubGraphQLResponse containing the first 100 repos
     */
    public GithubGraphQLResponse searchRepositoryQuery(String after, String created, Boolean searchInReadme){
        String query =
                "query SearchRepo($queryString: String!, $after: String) { "+
            "search(query: $queryString, type: REPOSITORY, first: 100, after: $after) { "+
                "repositoryCount "+
                        "pageInfo{"+
            "endCursor "+
            "hasNextPage "+
        "} "+
                "edges { "+
                        "cursor "+
                    "repository: node { "+
        "... on Repository { "+
                            "readme: object(expression: \"master:README.md\") { "+
            "... on Blob { "+
                                    "text "+
                               " } "+
                           " } "+
                            "url "+
                            "sshUrl "+
                            "isFork "+
                                   "name "+
                            "descriptionHTML "+
                            "stars:stargazers { "+
                                "totalCount "+
                            "}"+
                        "}"+
                    "}"+
                "}"+
            "}"+
        "}";
        Map<String, Object> variables = new HashMap<>(); // <3>
        if(searchInReadme){
            variables.put("queryString", "https://play.google.com/store/apps/details?id= in:readme,description created:"+created);
        }else{
            variables.put("queryString", "https://play.google.com/store/apps/details?id= in:description created:"+created);
        }
        variables.put("after", after );
        try {
            Response response = githubClient.executeSync(query, variables);
            return response.getObject(GithubGraphQLResponse.class,"search");
        } catch (ExecutionException e) {
            throw new RuntimeException("Error while getting repos",e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error while getting repos",e);
        }
    }



    private String getPattern(ZonedDateTime start,ZonedDateTime end){
        return   this.formater.format(start)+".."+this.formater.format(end);
    }




}
