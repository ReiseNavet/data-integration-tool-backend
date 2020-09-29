package services;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owlapi.model.OWLOntology;

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
import algorithms.subsumptionmatching.DefinitionSubsumptionMatcherSigmoid_revised;
import algorithms.subsumptionmatching.LexicalSubsumptionMatcherSigmoid;

/*
* Takes in input from user and return the most fitted algorithm for the use
*/
public class AlgorithmPicker {

  private List<AlignmentProcess> matchingAlgorithms;


  public AlgorithmPicker(OWLOntology source, OWLOntology target){

    initializeMatchingAlgorithms(source, target);
  }

  /*
  * Manually add all existing algorithms to the list when initializing class
  */
  public List<AlignmentProcess> initializeMatchingAlgorithms(OWLOntology source, OWLOntology target){

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
    matchingAlgorithms.add(new DefinitionSubsumptionMatcherSigmoid_revised());
    matchingAlgorithms.add(new LexicalSubsumptionMatcherSigmoid());

    return matchingAlgorithms;
  }

  public static AlignmentProcess pickAlgorithm(
    OWLOntology source, 
    OWLOntology target, 
    boolean equivalence, 
    boolean subsumption
    ){

      /*
      For now this will take the input and always return the 
      EquivalenceAlgorithm (used in BasicMatcher). Should, in 
      the end, return fitting algorithms for the input. 
      */

      return new BasicEQMatcher(source, target);

    }
}
