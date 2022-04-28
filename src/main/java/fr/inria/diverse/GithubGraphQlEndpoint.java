package fr.inria.diverse;

import fr.inria.diverse.model.graphql.GithubGraphQLRepository;
import fr.inria.diverse.model.graphql.GithubGraphQLResponse;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.graphql.client.Response;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@ApplicationScoped
public class GithubGraphQlEndpoint {
    @Inject
    @GraphQLClient("github-graphql")
    DynamicGraphQLClient githubClient;
    public List<GithubGraphQLRepository> getAllRepositoriesMatchingDescription(){
        return this.getRepositoriesMatchingDescription(null).getEdges().stream().map(edge -> edge.getRepository()).collect(Collectors.toList());
    }

    public GithubGraphQLResponse getRepositoriesMatchingReadme(String after){
        String query =
                "query SearchRepo($queryString: String!, $after: String) { "+
            "search(query: $queryString, type: REPOSITORY, first: 100, , after: $after) { "+
                "repositoryCount "+
                        "pageInfo{"+
            "hasNextPage"+
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
        variables.put("queryString", "https://play.google.com/store/apps/details?id= in:readme,description");
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

    public GithubGraphQLResponse getRepositoriesMatchingDescription(String after){
        String query =
                "query SearchRepo($queryString: String!, $after: String) { "+
                        "search(query: $queryString, type: REPOSITORY, first: 100, , after: $after) { "+
                        "repositoryCount "+
                        "pageInfo{"+
                        "hasNextPage"+
                        "} "+
                        "edges { "+
                        "cursor "+
                        "repository: node { "+
                        "... on Repository { "+
                        "isFork "+
                        "url "+
                        "sshUrl "+
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
        variables.put("queryString", "https://play.google.com/store/apps/details?id= in:description");
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
}
