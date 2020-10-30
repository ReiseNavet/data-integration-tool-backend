import java.io.File;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

import services.AlgorithmPicker;
import services.AlgorithmRunner;

public class AlignmentCombinerTest {
    private String sourceFileLocation = TestConfig.sourceFileLocation;
    private String targetFileLocation = TestConfig.targetFileLocation;
    private File sourceFile = new File(sourceFileLocation);
    private File targetFile = new File(targetFileLocation);

    @Test
    public void testRun() throws Exception {
        boolean useEquivalence = true;
        boolean useSubsumption = true;

        AlgorithmPicker picker = new AlgorithmPicker();
        AlgorithmRunner runner = new AlgorithmRunner();
        
        assertNotNull(runner.run(sourceFile, targetFile, 
          picker.pickAlgorithms(sourceFile, targetFile, useEquivalence, useSubsumption)));
    }

}
