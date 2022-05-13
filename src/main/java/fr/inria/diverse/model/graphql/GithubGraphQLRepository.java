package fr.inria.diverse.model.graphql;

public class GithubGraphQLRepository {
    private GithubGraphQLReadme readme;
    private Boolean isFork;
    private String name;
    private String descriptionHTML;
    private GithubGraphQLStars stars;
    private String url;
    private String sshUrl;
    private GithubGraphQLOwner owner;

    public GithubGraphQLReadme getReadme() {
        return readme;
    }

    public void setReadme(GithubGraphQLReadme readme) {
        this.readme = readme;
    }

    public Boolean getFork() {
        return isFork;
    }

    public void setFork(Boolean fork) {
        isFork = fork;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptionHTML() {
        return descriptionHTML;
    }

    public void setDescriptionHTML(String descriptionHTML) {
        this.descriptionHTML = descriptionHTML;
    }

    public GithubGraphQLStars getStars() {
        return stars;
    }

    public void setStars(GithubGraphQLStars stars) {
        this.stars = stars;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSshUrl() {
        return sshUrl;
    }

    public void setSshUrl(String sshUrl) {
        this.sshUrl = sshUrl;
    }

    public GithubGraphQLOwner getOwner() { return owner; }

    public void setOwner(GithubGraphQLOwner owner) { this.owner = owner; }
}
