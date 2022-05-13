package fr.inria.diverse;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.inria.diverse.api.client.GithubGraphQlEndpoint;
import fr.inria.diverse.api.client.GithubRestEndpoint;
import fr.inria.diverse.config.FileConfig;
import fr.inria.diverse.model.RawRepository;
import fr.inria.diverse.model.RawRepositoryList;
import fr.inria.diverse.model.Result;
import fr.inria.diverse.model.graphql.GithubGraphQLRepository;
import fr.inria.diverse.model.graphql.GithubGraphQLRepositoryList;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fr.inria.diverse.Utils.read;
import static fr.inria.diverse.Utils.save;

@ApplicationScoped
public class DataExtractor {
    Pattern p = Pattern.compile("(https?://play.google.com/store/apps/details\\?id=[a-zA-Z0-9.]*)");
    @Inject
    FileConfig config;
    @Inject
    GithubGraphQlEndpoint graphQlEndpoint;
    @Inject
    GithubRestEndpoint restEndpoint;
    public DataExtractor()   {

    }

    /**
     * Extract tuple of (repoUrl,sshRepoUrl,googlePlayUrl) and save it to result/res.json file
     * @return a set of Result, representing the tuple
     */
    public HashSet<Result> extract(){
        HashSet<Result> res = new HashSet<>();

        List<RawRepository> rawRepoHavingGplayUriInDesc =graphQlEndpoint.getAllRawRepositoriesHavingGplayLinkInDescription(true);
        res.addAll(extractFromRawResult(rawRepoHavingGplayUriInDesc ,config.missed_description_reposWithoutURI(),config.missed_description_reposWithMoreThanOneUri()));

        List<RawRepository> rawRepoHavingGplayUriInReadme =graphQlEndpoint.getAllRawRepositoriesHavingGplayLinkInReadme(true);
        //retrieveGithubReadme(rawRepoHavingGplayUriInReadme);
        res.addAll(extractFromRawResult(rawRepoHavingGplayUriInReadme ,config.missed_readme_reposWithoutURI(),config.missed_readme_reposWithMoreThanOneUri()));

        System.out.println("Found "+res.size()+" different result");
        save(config.final_result(),res);
        return res;
    }

    /**
     * Do the same thing as extract() but use the raw_data fetch by the api stored on results/*_raw.json
     */
    public void extractFromCheckPoint(){
        HashSet<Result> res = new HashSet<>();
        System.out.println("\n\n---------RESULTS by criteria---------");
        List<RawRepository> rawRepoHavingGplayUriInDesc = graphQlEndpoint.getAllRawRepositoriesHavingGplayLinkInDescriptionFromCheckPoint();
        res.addAll(extractFromRawResult(rawRepoHavingGplayUriInDesc ,config.missed_description_reposWithoutURI(),config.missed_description_reposWithMoreThanOneUri()));

        List<RawRepository> rawRepoHavingGplayUriInReadme = this.graphQlEndpoint.getAllRawRepositoriesHavingGplayLinkInReadmeFromCheckPoint();
        rawRepoHavingGplayUriInReadme= retrieveGithubReadme(rawRepoHavingGplayUriInReadme);
        res.addAll(extractFromRawResult(rawRepoHavingGplayUriInReadme ,config.missed_readme_reposWithoutURI(),config.missed_readme_reposWithMoreThanOneUri()));

        System.out.println("\n\n---------RESULTS---------");
        System.out.println("Found "+res.size()+" different result at all(different tuple ie tuple which have at least 1 attrbute not equals)");

        Set<String> test = new HashSet<>();
        res.forEach(repo -> test.add(repo.getGooglePlayUrl()));
        System.out.println("Found "+test.size()+" different gplay uri" );
        printResult();
        save(config.final_result(),res);
    }
    /**
     * Readme that are not located on master:README.md are ignored by the previous steps,
     * We need a way to extract the missing README. It is not possible to cover all cases
     * with the graphQL API, so we decide to implement this function that will search for missing readme
     * using REST API.
     * @param list the source RawRepoList
     * @return a new list with textContainingGplayUri filled by the missing README
     * Notes :
     * - if your quotas are met, the program simply wait until you obtain new credits
     * - if there is an error during REST call, the repo is simply skipped.
     * Todo : Handle error cases
     * Todo : Send notification when your credit are reached
     */
    private List<RawRepository> retrieveGithubReadme(List<RawRepository> list) {
        List<RawRepository> newList = list.parallelStream().map(repo ->{
            if (repo.getTextContainingGplayUri() == null || repo.getTextContainingGplayUri().isEmpty()) {
                String readme = restEndpoint.getReadme(repo.getName(), repo.getOwner());
                repo.setTextContainingGplayUri(readme);
            }
            return repo;
        }).collect(Collectors.toList());

        save(config.rawRepo_github_readme(),newList);
        return newList;
    }


    private List<Result> extractFromRawResult(List<RawRepository> repos,String repoWithoutUrlCheckpoint, String reposWithMoreThanOneUriCheckpoint){
        List<Result> results =new LinkedList<>();
        List<RawRepository> reposWithoutURI = new LinkedList<>();
        List<RawRepository> reposWithMoreThanOneUri = new LinkedList<>();
        repos.parallelStream().forEach(repo ->{
            Set<String> gPlayUris = extractGooglePlayUri(repo.getTextContainingGplayUri());
            int nbUri = gPlayUris.size();
            if(nbUri==1){
                String gPlayUri=gPlayUris.iterator().next();
                synchronized (results) {
                    results.add(new Result(repo.getRepoUrl(), repo.getSshRepoUrl(), gPlayUri));
                }
            }else if(nbUri==0){
                synchronized (reposWithoutURI) {
                    reposWithoutURI.add(repo);
                }
            }else{
                synchronized (reposWithMoreThanOneUri) {
                    reposWithMoreThanOneUri.add(repo);
                }
            }
        });
        save(repoWithoutUrlCheckpoint,reposWithoutURI);
        save(reposWithMoreThanOneUriCheckpoint,reposWithMoreThanOneUri);

        return results;
    }


    /**
     * Print result that are stored on results folder
     */
    private void printResult(){
        System.out.println("\n\n---------Missed Stats :result searching by description criteria----------");
        RawRepositoryList descWithoutUri = read(config.missed_description_reposWithoutURI());
        System.out.println("Found "+descWithoutUri.size()+ " repos without uri while it was suppose to have at least one in description");

        RawRepositoryList descWithMoreThanOneUri = read(config.missed_description_reposWithMoreThanOneUri());
        System.out.println("Found "+descWithMoreThanOneUri.size()+ " repos with more than one uri in description");

        System.out.println("\n---------Missed Stats :result searching by readme criteria----------");
        RawRepositoryList readmeWithoutUri = read(config.missed_readme_reposWithoutURI());
        System.out.println("Found "+readmeWithoutUri.size()+ " repos without uri while it was suppose to have at least one in readme");
        //adding special metric for repo with empty textcontainingGplayuri attribute issue #1
        long empty_null_readme_count =readmeWithoutUri.stream().filter(next ->  next.getTextContainingGplayUri()==null||next.getTextContainingGplayUri().isEmpty()).count();
        System.out.println(" --> Found "+empty_null_readme_count+" repos with an empty or null readme while it was suppose to have a readme and a gplay uri in that readme\n");

        RawRepositoryList readmeWithMoreThanOneUri = read(config.missed_readme_reposWithMoreThanOneUri());
        System.out.println("Found "+readmeWithMoreThanOneUri.size()+ " repos with more than one uri in readme");

    }


    /**
     * Extract google play uris from a string
     * @param source
     * @return
     */
    public Set<String> extractGooglePlayUri(String source){
        Matcher m = p.matcher(source);
        final Set<String> matches = new HashSet<>();
        while (m.find()) {
            matches.add(m.group(0));
        }
        return matches;
    }


}
