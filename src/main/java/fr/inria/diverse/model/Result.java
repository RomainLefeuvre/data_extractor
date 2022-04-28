package fr.inria.diverse.model;

public class Result {
    private String repoUrl;
    private String sshRepoUrl;
    private String googlePlayUrl;

    public Result(String repoUrl, String sshRepoUrl, String googlePlayUrl) {
        this.repoUrl = repoUrl;
        this.sshRepoUrl = sshRepoUrl;
        this.googlePlayUrl = googlePlayUrl;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getSshRepoUrl() {
        return sshRepoUrl;
    }

    public void setSshRepoUrl(String sshRepoUrl) {
        this.sshRepoUrl = sshRepoUrl;
    }

    public String getGooglePlayUrl() {
        return googlePlayUrl;
    }

    public void setGooglePlayUrl(String googlePlayUrl) {
        this.googlePlayUrl = googlePlayUrl;
    }
}
