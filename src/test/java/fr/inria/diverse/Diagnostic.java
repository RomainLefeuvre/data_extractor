package fr.inria.diverse;

import fr.inria.diverse.api.client.GithubGraphQlEndpoint;
import fr.inria.diverse.model.RawRepository;
import fr.inria.diverse.model.graphql.Criteria;
import fr.inria.diverse.model.graphql.GithubGraphQLRepository;
import fr.inria.diverse.model.graphql.GithubGraphQLResponse;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class Diagnostic {

    @Inject
    GithubGraphQlEndpoint endpoint;

    @Test
    public void test() {
        //de.extract();
        //de.extractFromCheckPoint();

        GithubGraphQLResponse f = endpoint.searchRepositoryQuery(null,"null",new customCriteria());
    }

    public class customCriteria implements Criteria {

        @Override
        public RawRepository getRawRepositoryFromGhRepo(GithubGraphQLRepository repo) {
            return null;
        }

        @Override
        public String getRawJsonCheckpointUri() {
            return null;
        }

        @Override
        public String getRawRepoCheckpointUri() { return null; }

        @Override
        public String getCriteriaName() {
            return null;
        }

        @Override
        public String getGraphQlQueryArg(String created) {
            return "That-level-again-unity";
        }

        @Override
        public int getDefaultIncrementInMinutes() {
            return 0;
        }
    }
}
