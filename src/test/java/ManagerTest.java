import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.Manager;

public class ManagerTest {
    public static final String baseSaveLocation = "files/_PHD_EVALUATION/OAEI2011/ONTOLOGIES/301302";
    public static final String sourceFileLocation = baseSaveLocation + "/301302-301.rdf";
    public static final String targetFileLocation = baseSaveLocation + "/301302-302.rdf";
    public static final String baseSaveLocationHandle = "files/temp/";

    @Test
    public void testHandle() throws Exception {
        boolean useEquivalence = true;
        boolean useSubsumption = true;

        System.out.println(sourceFileLocation);
        System.out.println(targetFileLocation);
        System.out.println(useEquivalence);
        System.out.println(useSubsumption);

        URIAlignment result = (new Manager()).handle(sourceFileLocation, targetFileLocation, useEquivalence, useSubsumption, baseSaveLocationHandle);
        assertNotNull(result);
    }
}
