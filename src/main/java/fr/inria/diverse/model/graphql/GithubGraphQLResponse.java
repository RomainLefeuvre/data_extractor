package fr.inria.diverse.model.graphql;

import fr.inria.diverse.model.GithubRepository;

import java.util.List;

public class GithubGraphQLResponse {
    public GithubGraphQLResponse() {
    }

    private int repositoryCount;
    private GithubGraphQLPageInfo pageInfo;
    private List<GithubGraphQLEdges> edges;

    //Getters and setters
    public int getRepositoryCount() {
        return repositoryCount;
    }
    public void setRepositoryCount(int repositoryCount) {
        this.repositoryCount = repositoryCount;
    }

    public GithubGraphQLPageInfo getPageInfo() {
        return pageInfo;
    }
    public void setPageInfo(GithubGraphQLPageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<GithubGraphQLEdges> getEdges() {
        return edges;
    }
    public void setEdges(List<GithubGraphQLEdges> edges) {
        this.edges = edges;
    }


}