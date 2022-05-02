package fr.inria.diverse.model.graphql;

public class GithubGraphQLPageInfo {
    public GithubGraphQLPageInfo() {
    }

    private boolean hasNextPage;
    private String endCursor;

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public String getEndCursor() {
        return endCursor;
    }

    public void setEndCursor(String endCursor) {
        this.endCursor = endCursor;
    }
}