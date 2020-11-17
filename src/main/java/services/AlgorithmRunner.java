package services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.interfaces.Algorithm;
import services.enums.SemanticRelation;

public class AlgorithmRunner {
  /**
   * Run each algorithm and return each of their alignment result.
   */
  public Map<SemanticRelation, List<URIAlignment>> run(File onto1, File onto2, Map<SemanticRelation, List<Algorithm>> algorithms)
      throws Exception {
    Map<SemanticRelation, List<URIAlignment>> toReturn = new HashMap<SemanticRelation, List<URIAlignment>>();

    // runs the picked algorithms for the user-selected semantic relations
    for (SemanticRelation relation : algorithms.keySet()){
      List<URIAlignment> alignments = algorithmsToAlignments(onto1, onto2, algorithms.get(relation));
      toReturn.put(relation, alignments);
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

