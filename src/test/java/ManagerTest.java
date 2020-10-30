import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.Manager;

public class ManagerTest {
    @Test
    public void testHandle() throws Exception {
        boolean useEquivalence = true;
        boolean useSubsumption = true;

        URIAlignment result = (new Manager()).handle(TestConfig.sourceFileLocation, TestConfig.targetFileLocation, useEquivalence, useSubsumption, TestConfig.baseSaveLocationHandle);
        assertNotNull(result);
    }
}
