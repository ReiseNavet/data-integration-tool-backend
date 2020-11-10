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
      eqAlgorithms.add(new BasicEQMatcher()); //Did not use this as it was not used in semantic matcher
      double cc = profiles.get("cc");
      double dc = profiles.get("dc");
      double pf = profiles.get("pf");
      double sp = profiles.get("sp");
      double lc = profiles.get("lc");
      if (cc >= 0.5){
        eqAlgorithms.add(new WordEmbeddingMatcherSigmoid(cc));
      }
      if (dc >= 0.5){
        eqAlgorithms.add(new DefinitionEquivalenceMatcherSigmoid(dc));
      }
      if (pf >= 0.5){
        eqAlgorithms.add(new PropertyEquivalenceMatcherSigmoid(pf));
      }
      if (sp >= 0.5){
        eqAlgorithms.add(new GraphEquivalenceMatcherSigmoid(sp));
      }
      if (lc >= 0.5){
        eqAlgorithms.add(new LexicalEquivalenceMatcherSigmoid(lc));
      }
      toReturn.put(SemanticRelation.Equivalence, eqAlgorithms);
    }
    if (subsumption){
      List<Algorithm> subAlgorithms = new ArrayList<Algorithm>();
      double cf = profiles.get("cf");
      double dc = profiles.get("dc");
      double sp = profiles.get("sp");
      double lc = profiles.get("lc");
      subAlgorithms.add(new BasicSubsumptionMatcher()); //Did not use this as it was not used in semantic matcher
      if (cf >= 0.5){
        subAlgorithms.add(new CompoundMatcherSigmoid(cf));
      }
      if (dc >= 0.5){
        subAlgorithms.add(new DefinitionSubsumptionMatcherSigmoid(dc));
      }
      if (sp >= 0.5){
        subAlgorithms.add(new ContextSubsumptionMatcherSigmoid(sp));
      }
      if (lc >= 0.5){
        subAlgorithms.add(new LexicalSubsumptionMatcherSigmoid(lc));
      }
      toReturn.put(SemanticRelation.Subsumption, subAlgorithms);
    }
    return toReturn;
  }
}
