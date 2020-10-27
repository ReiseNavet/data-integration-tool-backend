package services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithms.equivalencematching.BasicEQMatcher;
import algorithms.equivalencematching.DefinitionEquivalenceMatcherSigmoid;
import algorithms.equivalencematching.GraphEquivalenceMatcherSigmoid;
import algorithms.equivalencematching.LexicalEquivalenceMatcherSigmoid;
import algorithms.equivalencematching.PropertyEquivalenceMatcherSigmoid;
import algorithms.equivalencematching.WordEmbeddingMatcherSigmoid;
import algorithms.ontologyprofiling.OntologyProfiler;
import algorithms.subsumptionmatching.BasicSubsumptionMatcher;
import algorithms.subsumptionmatching.CompoundMatcherSigmoid;
import algorithms.subsumptionmatching.ContextSubsumptionMatcherSigmoid;
import algorithms.subsumptionmatching.DefinitionSubsumptionMatcherSigmoid;
import algorithms.subsumptionmatching.LexicalSubsumptionMatcherSigmoid;
import services.enums.SemanticRelation;
import services.interfaces.Algorithm;
import services.settings.AlgorithmSettings;

/*
* Takes in input from user and return the most fitted algorithm for the use
*/
public class AlgorithmPicker {
  public AlgorithmPicker() {

  }

  /*
   * For now this will take the input and always return the EquivalenceAlgorithm
   * (used in BasicMatcher). Should, in the end, return fitting algorithms for the
   * input.
   */
  public Map<SemanticRelation, List<Algorithm>> pickAlgorithms(File source, File target, boolean equivalence, boolean subsumption)
      throws Exception {

    String vectorFile = AlgorithmSettings.VECTORFILE;
    Map<SemanticRelation, List<Algorithm>> toReturn = new HashMap<SemanticRelation, List<Algorithm>>();

    //Getting the profile-scores for each of the algorithms on how well they will perform on our source and target and corpus
    Map<String, Double> profiles  = OntologyProfiler.computeOntologyProfileScores(source, target, vectorFile, equivalence, subsumption);

    //Adding the algorithms to be returned if their profile-score is >=0.5
    if (equivalence){
      List<Algorithm> eqAlgorithms = new ArrayList<Algorithm>();
      eqAlgorithms.add(new BasicEQMatcher());
      if (profiles.get("cc") >= 0.5){
        eqAlgorithms.add(new WordEmbeddingMatcherSigmoid());
      }
      if (profiles.get("dc") >= 0.5){
        eqAlgorithms.add(new DefinitionEquivalenceMatcherSigmoid());
      }
      if (profiles.get("pf") >= 0.5){
        eqAlgorithms.add(new PropertyEquivalenceMatcherSigmoid());
      }
      if (profiles.get("sp") >= 0.5){
        eqAlgorithms.add(new GraphEquivalenceMatcherSigmoid());
      }
      if (profiles.get("lc") >= 0.5){
        eqAlgorithms.add(new LexicalEquivalenceMatcherSigmoid());
      }
      toReturn.put(SemanticRelation.Equivalence, eqAlgorithms);
    }
    if (subsumption){
      List<Algorithm> subAlgorithms = new ArrayList<Algorithm>();
      subAlgorithms.add(new BasicSubsumptionMatcher());
      if (profiles.get("cf") >= 0.5){
        subAlgorithms.add(new CompoundMatcherSigmoid());
      }
      if (profiles.get("dc") >= 0.5){
        subAlgorithms.add(new DefinitionSubsumptionMatcherSigmoid());
      }
      if (profiles.get("sp") >= 0.5){
        subAlgorithms.add(new ContextSubsumptionMatcherSigmoid());
      }
      if (profiles.get("lc") >= 0.5){
        subAlgorithms.add(new LexicalSubsumptionMatcherSigmoid());
      }
      toReturn.put(SemanticRelation.Subsumption, subAlgorithms);
    }
    return toReturn;
  }
}
