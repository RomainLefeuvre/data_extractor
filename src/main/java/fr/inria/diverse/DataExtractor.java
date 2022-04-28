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

    GitHub github;
    @Inject
    GithubGraphQlEndpoint graphQlEndpoint;
    public DataExtractor()   {
        /*connectToGithub();
        int t = 0;
        try {
            t = github.getRateLimit().getLimit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(t);*/

    }

    public void extract() throws IOException {

        PagedIterable<GHRepository> t= github.searchRepositories().q("https://play.google.com/store/apps/details in:description").list().withPageSize(100);
   /* int c=0;
        PagedIterator<GHRepository> it= t.iterator();
        while(it.hasNext()){
           List<GHRepository> res = it.nextPage();
            c++;
            System.out.println("ok "+c);

        }*/
        List<GHRepository> l =t.iterator().nextPage();
        List<GHRepository> list = t.toList();
        System.out.println("ok");
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
            Set<String> gPlayUris = extractGooglePlayUri(repo.getdescriptionHTML());
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


    private void connectToGithub()  {


        try {
           github = GitHubBuilder.fromPropertyFile().build();
        } catch (IOException e) {
            throw new RuntimeException("Error while connecting to Github",e);
        }
    }

    private <T> void save(String fileName,T object){
        try {
            mapper.writeValue(new File("results/desc_raw.json"),object);
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
