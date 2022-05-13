package fr.inria.diverse.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "api")
public interface GithubApiConfig {
    public String githubToken();
}
