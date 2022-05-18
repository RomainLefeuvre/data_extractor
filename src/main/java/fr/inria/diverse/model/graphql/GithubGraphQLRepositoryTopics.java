package fr.inria.diverse.model.graphql;

import java.util.List;

public class GithubGraphQLRepositoryTopics {
    List<GithubGraphQLEdges<GithubGraphQLTopic>> edges;

    public List<GithubGraphQLEdges<GithubGraphQLTopic>> getEdges() {
        return edges;
    }

    public void setEdges(List<GithubGraphQLEdges<GithubGraphQLTopic>> edges) {
        this.edges = edges;
    }
}
