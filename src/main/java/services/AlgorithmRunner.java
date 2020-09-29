package services;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang.NotImplementedException;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.interfaces.Algorithm;

public class AlgorithmRunner {
    URIAlignment[] run(File onto1, File onto2, Algorithm[] algorithms) {
        return (URIAlignment[]) Arrays.stream(algorithms).map((algorithm) -> {
            try {
                return algorithm.run(onto1, onto2);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        })
        .toArray();
    }
}
