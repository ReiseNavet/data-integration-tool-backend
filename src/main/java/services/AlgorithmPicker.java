package services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

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
import rita.wordnet.jwnl.JWNLException;
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
  public Algorithm[] pickAlgorithms(File source, File target, boolean equivalence, boolean subsumption)
      throws OWLOntologyCreationException, JWNLException, IOException {

    String vectorFile = AlgorithmSettings.VECTORFILE;
    ArrayList<AlignmentProcess> toReturn = new ArrayList<AlignmentProcess>();
    ArrayList<AlignmentProcess> eqAlgorithms = new ArrayList<AlignmentProcess>();
    ArrayList<AlignmentProcess> subAlgorithms = new ArrayList<AlignmentProcess>();

    //Getting the profile-scores for each of the algorithms on how well they will perform on our source and target and corpus
    Map<String, Double> profiles  = OntologyProfiler.computeOntologyProfileScores(source, target, vectorFile, equivalence, subsumption);

    //Adding the algorithms to be returned if their profile-score is >=0.5
    eqAlgorithms.add(new BasicEQMatcher());
    subAlgorithms.add(new BasicSubsumptionMatcher());

    if (profiles.get("cf") >= 0.5){
      subAlgorithms.add(new CompoundMatcherSigmoid());
    }
    if (profiles.get("cc") >= 0.5){
      eqAlgorithms.add(new WordEmbeddingMatcherSigmoid());
    }
    if (profiles.get("dc") >= 0.5){
      eqAlgorithms.add(new DefinitionEquivalenceMatcherSigmoid());
      subAlgorithms.add(new DefinitionSubsumptionMatcherSigmoid());
    }
    if (profiles.get("pf") >= 0.5){
      eqAlgorithms.add(new PropertyEquivalenceMatcherSigmoid());
    }
    if (profiles.get("sp") >= 0.5){
      eqAlgorithms.add(new GraphEquivalenceMatcherSigmoid());
      subAlgorithms.add(new ContextSubsumptionMatcherSigmoid());
    }
    if (profiles.get("lc") >= 0.5){
      eqAlgorithms.add(new LexicalEquivalenceMatcherSigmoid());
      subAlgorithms.add(new LexicalSubsumptionMatcherSigmoid());
    }

    if (equivalence) {
      toReturn.addAll(eqAlgorithms);
    }
    if (subsumption) {
      toReturn.addAll(subAlgorithms);
    }
    return (Algorithm[])toReturn.toArray();
  }
}
