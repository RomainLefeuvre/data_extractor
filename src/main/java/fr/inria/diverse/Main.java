package fr.inria.diverse;

import fr.inria.diverse.config.FileConfig;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.inject.Inject;

@QuarkusMain
public class Main implements QuarkusApplication  {
    @Inject
    DataExtractor de;
    @Inject
    FileConfig fc;
        @Override
        public int run(String... args) throws Exception {
            de.extract();
            //de.extractFromCheckPoint();
            return 0;
    }
}