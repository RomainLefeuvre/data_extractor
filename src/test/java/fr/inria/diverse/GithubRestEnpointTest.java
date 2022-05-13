package fr.inria.diverse;

import fr.inria.diverse.api.client.GithubRestEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class GithubRestEnpointTest {
    @Inject
    GithubRestEndpoint enpoint;

    @Test
    public void getReadmeTest(){
        String readme =enpoint.getReadme("gemoc-studio","eclipse");
        Assertions.assertNotNull(readme);
        Assertions.assertFalse(readme.isEmpty());
    }
}
