package fr.inria.diverse;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.inria.diverse.model.Result;
import fr.inria.diverse.model.graphql.GithubGraphQLRepository;
import fr.inria.diverse.model.graphql.GithubRepositoryList;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class DataExtractor {
    ObjectMapper mapper = new ObjectMapper();

    @Inject
    GithubGraphQlEndpoint graphQlEndpoint;
    public DataExtractor()   {

    }

    public void extract(){
        HashSet<Result> res = new HashSet<>();
        res.addAll(extractFromDescription());
        res.addAll(extractFromReadme());
        System.out.println("Found "+res.size()+" different result");
        save("results/res.json",res);
    }

    public void extractFromCheckPoint(){
        HashSet<Result> res = new HashSet<>();
        GithubRepositoryList desc = this.read("results/desc_raw.json");
        GithubRepositoryList readme = this.read("results/readme_raw.json");

        res.addAll(extractFromDescription(desc));
        res.addAll(extractFromReadme(readme));
        System.out.println("Found "+res.size()+" different result");

        Set<String> test = new HashSet<>();
        res.forEach(repo -> test.add(repo.getGooglePlayUrl()));
        System.out.println("Found "+test.size()+" different gplay uri" );
        printResult();
        save("results/res.json",res);
    }
    public void printResult(){
        GithubRepositoryList descWithoutUri = this.read("results/missed/description/reposWithoutURI.json");
        System.out.println("Found "+descWithoutUri.size()+ " repos without uri while it was suppose to have at least one in description");

        GithubRepositoryList descWithMoreThanOneUri = this.read("results/missed/description/reposWithMoreThanOneUri.json");
        System.out.println("Found "+descWithMoreThanOneUri.size()+ " repos with more than one uri in description");

        GithubRepositoryList readmeWithoutUri = this.read("results/missed/readme/reposWithoutURI.json");
        System.out.println("Found "+readmeWithoutUri.size()+ " repos without uri while it was suppose to have at least one in readme");

        GithubRepositoryList readmeWithMoreThanOneUri = this.read("results/missed/readme/reposWithMoreThanOneUri.json");
        System.out.println("Found "+readmeWithMoreThanOneUri.size()+ " repos with more than one uri in readme");

    }
    public List<Result> extractFromDescription(){
       List<GithubGraphQLRepository> repos= graphQlEndpoint.getAllRepositories(false);
       this.save("results/desc_raw.json",repos);
       return extractFromDescription(repos);
    }

    public List<Result> extractFromDescription(List<GithubGraphQLRepository> repos){
        List<Result> results =new LinkedList<>();
        List<GithubGraphQLRepository> reposWithoutURI = new LinkedList<>();
        List<GithubGraphQLRepository> reposWithMoreThanOneUri = new LinkedList<>();
        repos.parallelStream().forEach(repo ->{
            Set<String> gPlayUris = extractGooglePlayUri(repo.getDescriptionHTML());
            int nbUri = gPlayUris.size();
            if(nbUri==1){
                String gPlayUri=gPlayUris.iterator().next();
                synchronized (results) {
                    results.add(new Result(repo.getUrl(), repo.getSshUrl(), gPlayUri));
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
        save("results/missed/description/reposWithoutURI.json",reposWithoutURI);
        save("results/missed/description/reposWithMoreThanOneUri.json",reposWithMoreThanOneUri);

        return results;
    }
    public List<Result> extractFromReadme(){
        List<GithubGraphQLRepository> repos= graphQlEndpoint.getAllRepositories(true);
        this.save("results/readme_raw.json",repos);
        return extractFromReadme(repos);
    }

    public List<Result> extractFromReadme(List<GithubGraphQLRepository> repos){
        List<Result> results =new LinkedList<>();
        List<GithubGraphQLRepository> reposWithoutURI = new LinkedList<>();
        List<GithubGraphQLRepository> reposWithMoreThanOneUri = new LinkedList<>();

        repos.parallelStream().forEach(repo ->{
            if (repo.getReadme() !=null && repo.getReadme().getText() != null) {
                Set<String> gPlayUris = extractGooglePlayUri(repo.getReadme().getText());
                int nbUri = gPlayUris.size();
                if (nbUri == 1) {
                    String gPlayUri = gPlayUris.iterator().next();
                    synchronized (results) {
                        results.add(new Result(repo.getUrl(), repo.getSshUrl(), gPlayUri));
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
            }
                });
            save("results/missed/readme/reposWithoutURI.json", reposWithoutURI);
            save("results/missed/readme/reposWithMoreThanOneUri.json", reposWithMoreThanOneUri);

        return results;
    }

    private <T> void save(String fileName,T object){
        try {
            File f =new File(fileName);
            File parent = f.getParentFile();
            f.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(f,object);
        } catch (IOException e) {
            throw new RuntimeException("Error while saving results",e);
        }
    }

    private GithubRepositoryList read(String fileName){
        try {
            File f =new File(fileName);
           return mapper.readValue(f, GithubRepositoryList.class);
        } catch (IOException e) {
            throw new RuntimeException("Error while getting checkpoint",e);
        }
    }

    public Set<String> extractGooglePlayUri(String source){
        Pattern p = Pattern.compile("(https://play.google.com/store/apps/details\\?id=[a-zA-Z0-9.]*)");
        Matcher m = p.matcher(source);
        final Set<String> matches = new HashSet<>();
        while (m.find()) {
            matches.add(m.group(0));
        }

        return matches;
    }
}
