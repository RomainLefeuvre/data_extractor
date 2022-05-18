package fr.inria.diverse.api.client;

import fr.inria.diverse.config.FileConfig;
import fr.inria.diverse.model.RawRepository;
import fr.inria.diverse.model.graphql.Criteria;
import fr.inria.diverse.model.graphql.GithubGraphQLRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DescriptionCriteria implements Criteria {
    @Inject
    FileConfig config;

    @Override
    public RawRepository getRawRepositoryFromGhRepo(GithubGraphQLRepository repo) {
        List<String> topics=repo.getRepositoryTopics().getEdges().stream().map(edge -> edge.getNode().getName()).collect(Collectors.toList());
        return new RawRepository(repo.getUrl(),repo.getName(),repo.getOwner().getLogin(), repo.getSshUrl(), repo.getDescriptionHTML(), topics);
    }

    @Override
    public String getRawJsonCheckpointUri() {
        return config.rawJson_github_description();
    }

    @Override
    public String getRawRepoCheckpointUri() {
        return config.rawRepo_github_description();
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