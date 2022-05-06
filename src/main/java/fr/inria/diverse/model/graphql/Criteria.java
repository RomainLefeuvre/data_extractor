package fr.inria.diverse.model.graphql;

import fr.inria.diverse.model.RawRepository;

public interface Criteria {
        RawRepository getRawRepositoryFromGhRepo(GithubGraphQLRepository repo);

        String getRawCheckpointUri();

        String getCriteriaName();

        String getGraphQlQueryArg(String created);

        int getDefaultIncrementInMinutes();

}