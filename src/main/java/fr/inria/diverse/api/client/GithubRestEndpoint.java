package fr.inria.diverse.api.client;

import fr.inria.diverse.config.GithubApiConfig;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@ApplicationScoped
public class GithubRestEndpoint {
    @Inject
    GithubApiConfig config;
    private GitHub github;
    public GithubRestEndpoint(){

    }

    public String getReadme(String repoName, String repoOwner)  {
        try {
            GHRepository repo = github.getRepository(repoOwner+"/"+repoName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(repo.getReadme().read()));
            String readme = bufferedReader.lines().collect(Collectors.joining("\n"));
            return readme;

        } catch (Exception e) {
            System.out.println("Error while getting readme from github api for "+repoOwner+"/"+repoName);
        }
        return "";
    }

    @PostConstruct
    private void init(){
        try {
            this.github=new GitHubBuilder().withOAuthToken(config.githubToken()).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
