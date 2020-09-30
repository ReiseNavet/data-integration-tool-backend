package services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import algorithms.equivalencematching.BasicEQMatcher;
import algorithms.equivalencematching.DefinitionEquivalenceMatcherSigmoid;
import algorithms.equivalencematching.GraphEquivalenceMatcherSigmoid;
import algorithms.equivalencematching.LexicalEquivalenceMatcherSigmoid;
import algorithms.equivalencematching.PropertyEquivalenceMatcherSigmoid;
import algorithms.equivalencematching.WordEmbeddingMatcherSigmoid;
import algorithms.subsumptionmatching.BasicSubsumptionMatcher;
import algorithms.subsumptionmatching.CompoundMatcherSigmoid;
import algorithms.subsumptionmatching.ContextSubsumptionMatcherSigmoid;
import algorithms.subsumptionmatching.DefinitionSubsumptionMatcherSigmoid;
import algorithms.subsumptionmatching.LexicalSubsumptionMatcherSigmoid;
import services.interfaces.Algorithm;

/*
* Takes in input from user and return the most fitted algorithm for the use
*/
public class AlgorithmPicker {

  private List<AlignmentProcess> matchingAlgorithms;

  public AlgorithmPicker(){
    initializeMatchingAlgorithms();
  }

  /*
  * Manually add all existing algorithms to the list when initializing class
  */
  public List<AlignmentProcess> initializeMatchingAlgorithms(){

    matchingAlgorithms = new ArrayList<AlignmentProcess>();

    /* Values used as default in each alorithm's run method
    String vectorFile = "./files/_PHD_EVALUATION/EMBEDDINGS/wikipedia_embeddings.txt";
		int slope = 3; 
		double rangeMin = 0.5; 
		double rangeMax = 0.7;
    double profileScore = 0.9;
    */
    
    //equivalencematching added to matchingAlgorithms
    matchingAlgorithms.add(new BasicEQMatcher());
    matchingAlgorithms.add(new DefinitionEquivalenceMatcherSigmoid());
    matchingAlgorithms.add(new GraphEquivalenceMatcherSigmoid());
    matchingAlgorithms.add(new LexicalEquivalenceMatcherSigmoid());
    matchingAlgorithms.add(new PropertyEquivalenceMatcherSigmoid());
    matchingAlgorithms.add(new WordEmbeddingMatcherSigmoid());

    //subsumptionmatching added to matchingAlgorithms
    matchingAlgorithms.add(new BasicSubsumptionMatcher());
    matchingAlgorithms.add(new CompoundMatcherSigmoid());
    matchingAlgorithms.add(new ContextSubsumptionMatcherSigmoid());
    matchingAlgorithms.add(new DefinitionSubsumptionMatcherSigmoid());
    matchingAlgorithms.add(new LexicalSubsumptionMatcherSigmoid());

    return matchingAlgorithms;
  }

  /*
   * For now this will take the input and always return the 
   * EquivalenceAlgorithm (used in BasicMatcher). Should, in 
   * the end, return fitting algorithms for the input. 
   */
  public Algorithm[] pickAlgorithms(File source, File target, boolean equivalence, boolean subsumption) {

    OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    OWLOntology sourceOntology;
    OWLOntology targetOntology;
    try {
      sourceOntology = ontologyManager.loadOntologyFromOntologyDocument(source); 
      targetOntology = ontologyManager.loadOntologyFromOntologyDocument(target);
    } 
    catch(Exception e) {
      e.printStackTrace();
      return new Algorithm[]{};
    }

    return new Algorithm[] { new BasicEQMatcher(sourceOntology, targetOntology) };

  }

}
