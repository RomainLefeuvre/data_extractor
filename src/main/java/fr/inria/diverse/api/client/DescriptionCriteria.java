package fr.inria.diverse.api.client;

import fr.inria.diverse.config.FileConfig;
import fr.inria.diverse.model.RawRepository;
import fr.inria.diverse.model.graphql.Criteria;
import fr.inria.diverse.model.graphql.GithubGraphQLRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
@ApplicationScoped
public class DescriptionCriteria implements Criteria {
    @Inject
    FileConfig config;

    @Override
    public RawRepository getRawRepositoryFromGhRepo(GithubGraphQLRepository repo) {
        return new RawRepository(repo.getUrl(), repo.getSshUrl(), repo.getDescriptionHTML());
    }

    @Override
    public String getRawCheckpointUri() {
        return config.raw_github_description();
    }

    @Override
    public String getCriteriaName() {
        return "description";
    }

    @Override
    public String getGraphQlQueryArg(String created) {
        return "://play.google.com/store/apps/details?id= in:description created:" + created;
    }

    @Override
    public int getDefaultIncrementInMinutes() {
        return 10* 60 * 24 * 60;

    }
}