package algorithms.utilities;

public class AlgorithmEstimater {
    /**
     * The runtime seems to scale with each inputs filesize. The estimate should have at highest ±20% error margin. LEM is the hardest to correctly estimate.
     * @param sourceSizeKB
     * @param targetSizeKB
     * @return
     */
    float estimateRuntime(float sourceSizeKB, float targetSizeKB){
        return 5.0f + sourceSizeKB * targetSizeKB * 3.0f / 2000.0f;
    }
}