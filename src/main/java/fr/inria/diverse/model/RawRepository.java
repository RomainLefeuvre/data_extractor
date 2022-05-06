package fr.inria.diverse.model;

public class RawRepository {
    private String repoUrl;
    private String sshRepoUrl;
    private String textContainingGplayUri;

    public RawRepository(String repoUrl, String sshRepoUrl, String textContainingGplayUri) {
        this.repoUrl = repoUrl;
        this.sshRepoUrl = sshRepoUrl;
        this.textContainingGplayUri = textContainingGplayUri;
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

    public String getTextContainingGplayUri() {
        return textContainingGplayUri;
    }

    public void setTextContainingGplayUri(String textContainingGplayUri) {
        this.textContainingGplayUri = textContainingGplayUri;
    }

}
