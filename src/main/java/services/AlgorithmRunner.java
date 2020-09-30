package services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.interfaces.Algorithm;

public class AlgorithmRunner {
  URIAlignment[] run(File onto1, File onto2, Algorithm[] algorithms) {
    List<URIAlignment> result = new ArrayList<URIAlignment>();

    for (Algorithm algorithm : algorithms) {
      try {
        result.add(algorithm.run(onto1, onto2));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return result.toArray(new URIAlignment[]{});
  }
}
