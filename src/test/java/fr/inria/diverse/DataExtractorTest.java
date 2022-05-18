package fr.inria.diverse;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@QuarkusTest
public class DataExtractorTest {
    @Inject
    DataExtractor de;
    @Test
    public void extractGooglePlayUriTest(){
        Set<String> res =de.extractGooglePlayUri("kjkjkhttps://play.google.com/store/apps/details?id=net.sourceforge.bochs\" rel=\"nofollow\">https://play.google.com/store/apps/details?id=net.sourceforge.bochs</a>\n</div>");
        Assertions.assertEquals("https://play.google.com/store/apps/details?id=net.sourceforge.bochs",res.iterator().next());
        Assertions.assertEquals(res.size(),1);
    }

    @Test
    public void extractGooglePlayUriTest2(){
        Set<String> res =de.extractGooglePlayUri("kjkjkhttps://play.google.com/store/apps/details?id=net.sourceforge.bochs\" rel=\"nofolhttps://play.google.com/store/apps/details?id=net.sourceforqsdsqdqsdge.bochslow\">https://play.google.com/store/apps/details?id=net.sourceforge.bochs</a>\n</div>");
        Assertions.assertEquals(2,res.size());
        res =de.extractGooglePlayUri("kjkjkhttps://play.google.com/store/apps\n/details?id=net.sourceforge.bochs");
        Assertions.assertEquals(1,res.size());
    }

}
