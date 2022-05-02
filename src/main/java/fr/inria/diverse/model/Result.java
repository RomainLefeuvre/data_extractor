package fr.inria.diverse.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return Objects.equals(repoUrl, result.repoUrl) && Objects.equals(sshRepoUrl, result.sshRepoUrl) && Objects.equals(googlePlayUrl, result.googlePlayUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repoUrl, sshRepoUrl, googlePlayUrl);
    }
}
