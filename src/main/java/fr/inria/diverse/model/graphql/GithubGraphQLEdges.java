package fr.inria.diverse.model.graphql;

public class GithubGraphQLEdges {
        private String cursor;
        private GithubGraphQLRepository repository;

        public String getCursor() {
            return cursor;
        }

        public void setCursor(String cursor) {
            this.cursor = cursor;
        }

        public GithubGraphQLRepository getRepository() {
            return repository;
        }

        public void setRepository(GithubGraphQLRepository repository) {
            this.repository = repository;
        }
    }