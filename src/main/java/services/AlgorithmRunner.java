package services;

import java.util.Arrays;

import org.apache.commons.lang.NotImplementedException;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.interfaces.Algorithm;

public class AlgorithmRunner {
    URIAlignment[] run(Object onto1, Object onto2, Algorithm[] algorithms){
        return (URIAlignment[]) Arrays.stream(algorithms)
        .map((algorithm) -> algorithm.run(onto1, onto2))
        .toArray();
    }
}
