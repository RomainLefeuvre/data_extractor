package fr.inria.diverse.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "results")
public interface FileConfig {
String missed_description_reposWithMoreThanOneUri();
String missed_readme_reposWithMoreThanOneUri();
String missed_description_reposWithoutURI();
String missed_readme_reposWithoutURI();
String raw_github_description();
String raw_github_readme();
String final_result();
}
