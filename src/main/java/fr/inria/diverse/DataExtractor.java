package fr.inria.diverse;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.inria.diverse.api.client.GithubGraphQlEndpoint;
import fr.inria.diverse.config.FileConfig;
import fr.inria.diverse.model.RawRepository;
import fr.inria.diverse.model.Result;
import fr.inria.diverse.model.graphql.RawRepositoryList;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.inria.diverse.Utils.save;

@ApplicationScoped
public class DataExtractor {
    ObjectMapper mapper = new ObjectMapper();
    Pattern p = Pattern.compile("(https://play.google.com/store/apps/details\\?id=[a-zA-Z0-9.]*)");

    @Inject
    FileConfig config;
    @Inject
    GithubGraphQlEndpoint graphQlEndpoint;
    public DataExtractor()   {

    }

    /**
     * Extract tuple of (repoUrl,sshRepoUrl,googlePlayUrl) and save it to result/res.json file
     * @return a set of Result, representing the tuple
     */
    public HashSet<Result> extract(){
        HashSet<Result> res = new HashSet<>();

        List<RawRepository> rawRepoHavingGplayUriInDesc =graphQlEndpoint.getAllRepositories(graphQlEndpoint.decriptionCriteria,true);
        res.addAll(extractFromRawResult(rawRepoHavingGplayUriInDesc ,config.missed_description_reposWithoutURI(),config.missed_description_reposWithMoreThanOneUri()));

        List<RawRepository> rawRepoHavingGplayUriInReadme =graphQlEndpoint.getAllRepositories(graphQlEndpoint.readmeCriteria,true);
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
        RawRepositoryList rawRepoHavingGplayUriInDesc = this.read(config.raw_github_description());
        res.addAll(extractFromRawResult(rawRepoHavingGplayUriInDesc ,config.missed_description_reposWithoutURI(),config.missed_description_reposWithMoreThanOneUri()));

        RawRepositoryList rawRepoHavingGplayUriInReadme = this.read(config.raw_github_readme());
        res.addAll(extractFromRawResult(rawRepoHavingGplayUriInReadme ,config.missed_readme_reposWithoutURI(),config.missed_readme_reposWithMoreThanOneUri()));

        System.out.println("Found "+res.size()+" different result");

        Set<String> test = new HashSet<>();
        res.forEach(repo -> test.add(repo.getGooglePlayUrl()));
        System.out.println("Found "+test.size()+" different gplay uri" );
        printResult();
        save(config.final_result(),res);
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
        RawRepositoryList descWithoutUri = this.read(config.missed_description_reposWithoutURI());
        System.out.println("Found "+descWithoutUri.size()+ " repos without uri while it was suppose to have at least one in description");

        RawRepositoryList descWithMoreThanOneUri = this.read(config.missed_description_reposWithMoreThanOneUri());
        System.out.println("Found "+descWithMoreThanOneUri.size()+ " repos with more than one uri in description");

        RawRepositoryList readmeWithoutUri = this.read(config.missed_readme_reposWithMoreThanOneUri());
        System.out.println("Found "+readmeWithoutUri.size()+ " repos without uri while it was suppose to have at least one in readme");

        RawRepositoryList readmeWithMoreThanOneUri = this.read(config.missed_readme_reposWithMoreThanOneUri());
        System.out.println("Found "+readmeWithMoreThanOneUri.size()+ " repos with more than one uri in readme");

    }



    private RawRepositoryList read(String fileName){
        try {
            File f =new File(fileName);
           return mapper.readValue(f, RawRepositoryList.class);
        } catch (IOException e) {
            throw new RuntimeException("Error while getting checkpoint",e);
        }
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
