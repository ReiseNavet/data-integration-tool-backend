import java.io.File;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

import services.AlgorithmPicker;

public class AlgorithmPickerTest {
    private String sourceFileLocation = TestConfig.sourceFileLocation;
    private String targetFileLocation = TestConfig.targetFileLocation;
    private File sourceFile = new File(sourceFileLocation);
    private File targetFile = new File(targetFileLocation);

    @Test
    public void testPicker() throws Exception {
        boolean useEquivalence = true;
        boolean useSubsumption = true;

        AlgorithmPicker picker = new AlgorithmPicker();
        assertNotNull(picker.pickAlgorithms(sourceFile, targetFile, useEquivalence, useSubsumption));
    }

}
