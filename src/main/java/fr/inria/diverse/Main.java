package fr.inria.diverse;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.inject.Inject;

@QuarkusMain
public class Main implements QuarkusApplication  {
    @Inject
    DataExtractor de;
        @Override
        public int run(String... args) throws Exception {
            //de.extract();
            de.extractFromCheckPoint();
            return 0;
    }
}