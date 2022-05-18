package fr.inria.diverse.model.graphql;

public class GithubGraphQLEdges<T> {
        private String cursor;
        private T node;

        public String getCursor() {
            return cursor;
        }

        public void setCursor(String cursor) {
            this.cursor = cursor;
        }

        public T getNode() {
            return node;
        }

        public void setNode(T repository) {
            this.node = repository;
        }
    }