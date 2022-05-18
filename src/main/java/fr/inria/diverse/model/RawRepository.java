package fr.inria.diverse.model;

import java.util.List;

public class RawRepository {
    private String repoUrl;
    private String name;
    private String owner;
    private String sshRepoUrl;
    private String textContainingGplayUri;
    private List<String> topics;

    public RawRepository(String repoUrl, String name, String owner, String sshRepoUrl, String textContainingGplayUri, List<String> topics) {
        this.repoUrl = repoUrl;
        this.name = name;
        this.owner = owner;
        this.sshRepoUrl = sshRepoUrl;
        this.textContainingGplayUri = textContainingGplayUri;
        this.topics=topics;
    }

    public RawRepository() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
