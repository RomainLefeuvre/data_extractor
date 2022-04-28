package fr.inria.diverse.model.graphql;

public class GithubGraphQLPageInfo {
    public GithubGraphQLPageInfo() {
    }

    public boolean hasNextPage;

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
}