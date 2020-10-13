package services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.interfaces.Algorithm;
import services.enums.AlgorithmType;

public class AlgorithmRunner {
  Map<AlgorithmType, List<URIAlignment>> run(File onto1, File onto2, Map<AlgorithmType, List<Algorithm>> algorithms)
      throws Exception {
    Map<AlgorithmType, List<URIAlignment>> toReturn = new HashMap<AlgorithmType, List<URIAlignment>>();
    for (AlgorithmType algorithmType : algorithms.keySet()){
      List<URIAlignment> alignments = algorithmsToAlignments(onto1, onto2, algorithms.get(algorithmType));
      toReturn.put(algorithmType, alignments);
    }
    return toReturn;
  }
  List<URIAlignment> algorithmsToAlignments(File onto1, File onto2, List<Algorithm> algorithms) throws Exception {
    List<URIAlignment> result = new ArrayList<URIAlignment>();
    for (Algorithm algorithm : algorithms){
      result.add(algorithm.run(onto1, onto2));
    }
    return result;
  }

}

