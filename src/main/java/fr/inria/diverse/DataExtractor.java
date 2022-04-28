package fr.inria.diverse;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.inria.diverse.model.GithubRepository;
import fr.inria.diverse.model.Result;
import fr.inria.diverse.model.graphql.GithubGraphQLRepository;
import io.smallrye.graphql.client.Response;
import org.kohsuke.github.*;

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
        List<Result> res =extractFromDescription();
        res.addAll(extractFromReadme());
        save("results/res.json",res);
    }

    public List<Result> extractFromDescription(){
       List<GithubGraphQLRepository> repos= graphQlEndpoint.getAllRepositoriesMatchingDescription();
       this.save("results/desc_raw.json",repos);
       return extractFromDescription(repos);
    }

    public List<Result> extractFromDescription(List<GithubGraphQLRepository> repos){
        List<Result> results =new LinkedList<>();
        List<GithubGraphQLRepository> reposWithoutURI = new LinkedList<>();
        List<GithubGraphQLRepository> reposWithMoreThanOneUri = new LinkedList<>();

        for(GithubGraphQLRepository repo : repos){
            Set<String> gPlayUris = extractGooglePlayUri(repo.getDescriptionHTML());
            int nbUri = gPlayUris.size();
            if(nbUri==1){
                String gPlayUri=gPlayUris.iterator().next();
                results.add(new Result(repo.getUrl(),repo.getSshUrl(),gPlayUri));
            }else if(nbUri==0){
                reposWithoutURI.add(repo);
            }else{
                reposWithMoreThanOneUri.add(repo);
            }
        }
        save("results/missed/description/reposWithoutURI.json",reposWithoutURI);
        save("results/missed/description/reposWithMoreThanOneUri.json",reposWithMoreThanOneUri);

        return results;
    }
    public List<Result> extractFromReadme(){
        List<GithubGraphQLRepository> repos= graphQlEndpoint.getAllRepositoriesMatchingReadme();
        this.save("results/readme_raw.json",repos);
        return extractFromReadme(repos);
    }

    public List<Result> extractFromReadme(List<GithubGraphQLRepository> repos){
        List<Result> results =new LinkedList<>();
        List<GithubGraphQLRepository> reposWithoutURI = new LinkedList<>();
        List<GithubGraphQLRepository> reposWithMoreThanOneUri = new LinkedList<>();

        for(GithubGraphQLRepository repo : repos) {
            if (repo.getReadme() !=null && repo.getReadme().getText() != null) {
                Set<String> gPlayUris = extractGooglePlayUri(repo.getReadme().getText());
                int nbUri = gPlayUris.size();
                if (nbUri == 1) {
                    String gPlayUri = gPlayUris.iterator().next();
                    results.add(new Result(repo.getUrl(), repo.getSshUrl(), gPlayUri));
                } else if (nbUri == 0) {
                    reposWithoutURI.add(repo);
                } else {
                    reposWithMoreThanOneUri.add(repo);
                }
            }
            save("results/missed/readme/reposWithoutURI.json", reposWithoutURI);
            save("results/missed/readme/reposWithMoreThanOneUri.json", reposWithMoreThanOneUri);
        }
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
