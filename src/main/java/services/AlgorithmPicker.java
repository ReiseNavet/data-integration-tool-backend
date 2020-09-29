package services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import services.interfaces.Algorithm;

import com.clarkparsia.sparqlowl.parser.antlr.SparqlOwlParser.sourceSelector_return;

import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owlapi.model.OWLOntology;

import algorithms.equivalencematching.BasicEQMatcher;
import algorithms.equivalencematching.DefinitionEquivalenceMatcher;
import algorithms.equivalencematching.DefinitionEquivalenceMatcherSigmoid;
import algorithms.equivalencematching.GraphEquivalenceMatcherSigmoid;
import algorithms.equivalencematching.LexicalEquivalenceMatcher;
import algorithms.equivalencematching.LexicalEquivalenceMatcherSigmoid;
import algorithms.equivalencematching.PropertyEquivalenceMatcher;
import algorithms.equivalencematching.PropertyEquivalenceMatcherSigmoid;
import algorithms.equivalencematching.WordEmbeddingMatcher;
import algorithms.equivalencematching.WordEmbeddingMatcherSigmoid;
import algorithms.subsumptionmatching.BasicSubsumptionMatcher;
import algorithms.subsumptionmatching.CompoundMatcher;
import algorithms.subsumptionmatching.CompoundMatcherSigmoid;
import algorithms.subsumptionmatching.ContextSubsumptionMatcher;
import algorithms.subsumptionmatching.ContextSubsumptionMatcherSigmoid;
import algorithms.subsumptionmatching.DefinitionSubsumptionMatcher;
import algorithms.subsumptionmatching.DefinitionSubsumptionMatcherSigmoid;
import algorithms.subsumptionmatching.DefinitionSubsumptionMatcherSigmoid_revised;
import algorithms.subsumptionmatching.LexicalSubsumptionMatcher;
import algorithms.subsumptionmatching.LexicalSubsumptionMatcherSigmoid;
import algorithms.ui.BasicSemanticMatcher;

/*
* Takes in input from user and return the most fitted algorithm for the use
*/
public class AlgorithmPicker {

  private List<AlignmentProcess> matchingAlgorithms;
  private List<AlignmentProcess> otherAlgorithms;

  public AlgorithmPicker(OWLOntology source, OWLOntology target){

    initializeMatchingAlgorithms(source, target);
  }

  /*
  * Manually add all existing algorithms to the list when initializing class
  */
  public List<AlignmentProcess> initializeMatchingAlgorithms(OWLOntology source, OWLOntology target){

    matchingAlgorithms = new ArrayList<AlignmentProcess>();
    
    //equivalencematching added to matchingAlgorithms
    matchingAlgorithms.add(new BasicEQMatcher(ontoFile1, ontoFile2));
    matchingAlgorithms.add(new DefinitionEquivalenceMatcher(onto1, onto2, vectorFile, weight));
    matchingAlgorithms.add(new DefinitionEquivalenceMatcherSigmoid(onto1, onto2, vectorFile, profileScore, slope, rangeMin, rangeMax));
    matchingAlgorithms.add(new GraphEquivalenceMatcherSigmoid(sourceOntology, targetOntology, profileScore, slope, rangeMin, rangeMax);
    matchingAlgorithms.add(new LexicalEquivalenceMatcher(ontoFile1, ontoFile2, weight));
    matchingAlgorithms.add(new LexicalEquivalenceMatcherSigmoid(onto1, onto2, profileScore, slope, rangeMin, rangeMax));
    matchingAlgorithms.add(new PropertyEquivalenceMatcher(ontoFile1, ontoFile2, weight));
    matchingAlgorithms.add(new PropertyEquivalenceMatcherSigmoid(ontoFile1, ontoFile2, profileScore, slope, rangeMin, rangeMax));
    matchingAlgorithms.add(new WordEmbeddingMatcher(onto1, onto2, vectorFile, weight));
    matchingAlgorithms.add(new WordEmbeddingMatcherSigmoid(onto1, onto2, vectorFile, weight));

    //subsumptionmatching added to matchingAlgorithms
    matchingAlgorithms.add(new BasicSubsumptionMatcher(onto1, onto2));
    matchingAlgorithms.add(new CompoundMatcher(onto1, onto2, profileScore));
    matchingAlgorithms.add(new CompoundMatcherSigmoid(onto1, onto2, profileScore, slope, rangeMin, rangeMax);
    matchingAlgorithms.add(new ContextSubsumptionMatcher(ontoFile1, ontoFile2, profileScore));
    matchingAlgorithms.add(new ContextSubsumptionMatcherSigmoid(onto1, onto2, profileScore, slope, rangeMin, rangeMax));
    matchingAlgorithms.add(new DefinitionSubsumptionMatcher(onto1, onto2, profileScore));
    matchingAlgorithms.add(new DefinitionSubsumptionMatcherSigmoid(onto1, onto2, profileScore, slope, rangeMin, rangeMax));
    matchingAlgorithms.add(new DefinitionSubsumptionMatcherSigmoid_revised(onto1, onto2, profileScore, slope, rangeMin, rangeMax));
    matchingAlgorithms.add(new LexicalSubsumptionMatcher(onto1, onto2, profileScore));
    matchingAlgorithms.add(new LexicalSubsumptionMatcherSigmoid(onto1, onto2, profileScore, slope, rangeMin, rangeMax));

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
