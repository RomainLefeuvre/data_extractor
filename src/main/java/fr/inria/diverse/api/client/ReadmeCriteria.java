package fr.inria.diverse.api.client;

import fr.inria.diverse.config.FileConfig;
import fr.inria.diverse.model.RawRepository;
import fr.inria.diverse.model.graphql.Criteria;
import fr.inria.diverse.model.graphql.GithubGraphQLRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
@ApplicationScoped
public class ReadmeCriteria implements Criteria {
    @Inject
    FileConfig config;

    @Override
    public RawRepository getRawRepositoryFromGhRepo(GithubGraphQLRepository repo) {
        String textContainingGplayUri = "";
        if (repo.getReadme() != null && repo.getReadme().getText() != null) {
            textContainingGplayUri = repo.getReadme().getText();
        }
        return new RawRepository(repo.getUrl(),repo.getName(),repo.getOwner().getLogin(), repo.getSshUrl(), textContainingGplayUri);
    }

    @Override
    public String getRawJsonCheckpointUri() {
        return config.rawJson_github_readme();
    }

    @Override
    public String getRawRepoCheckpointUri() {
                 return config.rawRepo_github_readme();

    }

    @Override
    public String getCriteriaName() {
        return "readme";
    }

    @Override
    public String getGraphQlQueryArg(String created) {
        return "://play.google.com/store/apps/details?id= in:readme created:" + created;
    }

    @Override
    public int getDefaultIncrementInMinutes() {
        return 60 * 24 * 60;

    }
}