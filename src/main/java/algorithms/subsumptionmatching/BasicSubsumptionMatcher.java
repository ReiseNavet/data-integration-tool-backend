package algorithms.subsumptionmatching;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import algorithms.utilities.WordNet;
import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicConfidence;
import fr.inrialpes.exmo.align.impl.ObjectAlignment;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.impl.rel.A5AlgebraRelation;
import rita.wordnet.jwnl.JWNLException;
import services.interfaces.Algorithm;

public class BasicSubsumptionMatcher extends ObjectAlignment implements AlignmentProcess, Algorithm {

	OWLOntology sourceOntology;
	OWLOntology targetOntology;

	public BasicSubsumptionMatcher(){}

	public BasicSubsumptionMatcher(OWLOntology onto1, OWLOntology onto2) {
		this.sourceOntology = onto1;
		this.targetOntology = onto2;
	}

	//run method
	public URIAlignment run(File ontoFile1, File ontoFile2) throws OWLOntologyCreationException, AlignmentException {
		return returnBasicSUBMatcherAlignment(ontoFile1, ontoFile2);
	}

	public static URIAlignment returnBasicSUBMatcherAlignment (File ontoFile1, File ontoFile2) throws OWLOntologyCreationException, AlignmentException {

		URIAlignment BasicSUBMatcherAlignment = new URIAlignment();

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);

		AlignmentProcess a = new BasicSubsumptionMatcher(onto1, onto2);
		a.init(ontoFile1.toURI(), ontoFile2.toURI());
		Properties params = new Properties();
		params.setProperty("", "");
		a.align((Alignment)null, params);	
		BasicAlignment BasicSUBAlignment = new BasicAlignment();

		BasicSUBAlignment = (BasicAlignment) (a.clone());

		BasicSUBMatcherAlignment = BasicSUBAlignment.toURIAlignment();

		BasicSUBMatcherAlignment.init( onto1.getOntologyID().getOntologyIRI().toURI(), onto2.getOntologyID().getOntologyIRI().toURI(), A5AlgebraRelation.class, BasicConfidence.class );

		return BasicSUBMatcherAlignment;

	}
	
	public void align(Alignment alignment, Properties param) throws AlignmentException {
		Map<String, Set<String>> hyponymMapOnto1 = new HashMap<String, Set<String>>();
		Map<String, Set<String>> hyponymMapOnto2 = new HashMap<String, Set<String>>();
		//get classes and corresponding hyponyms for onto 1
		try {
			hyponymMapOnto1 = createHyponymMap(sourceOntology);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JWNLException e) {
			e.printStackTrace();
		}

		//get classes and corresponding hyponyms for onto 2
		try {
			hyponymMapOnto2 = createHyponymMap(targetOntology);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JWNLException e) {
			e.printStackTrace();
		}

		//System.err.println("Finished creating the initial data structures");

		Set<String> sourceHyponyms = new HashSet<String>();
		Set<String> targetHyponyms = new HashSet<String>();

		
		int idCounter = 0;

		try {
			// Match classes
			for ( Object sourceObject: ontology1().getClasses() ){
				for ( Object targetObject: ontology2().getClasses() ){
					
					idCounter++;

					String source = ontology1().getEntityName(sourceObject);
					String target = ontology2().getEntityName(targetObject);

					if (hyponymMapOnto1.containsKey(source.toLowerCase())) {
					sourceHyponyms = hyponymMapOnto1.get(source.toLowerCase());

					}
									
					if (hyponymMapOnto2.containsKey(target.toLowerCase())) {
					targetHyponyms = hyponymMapOnto2.get(target.toLowerCase());

					}
					
					
					//if the source concept equals a hyponym of the target concept
					if (targetHyponyms != null && targetHyponyms.contains(source.toLowerCase())) {
						addAlignCell("BasicSubsumptionMatcher" +idCounter, sourceObject, targetObject, "&lt;", 0.6);
					} 


					//if the target concept equals a hyponym of the source concept
					if (sourceHyponyms != null && sourceHyponyms.contains(target.toLowerCase())) {
						addAlignCell("BasicSubsumptionMatcher" +idCounter, sourceObject, targetObject, "&gt;", 0.6);
					} 
					
					else {
						addAlignCell("BasicSubsumptionMatcher" +idCounter, sourceObject, targetObject, "&gt;", 0.0);
					}

				}

			}

		} catch (Exception e) { e.printStackTrace(); }
	}
	
	private static Map<String, Set<String>> createHyponymMap (OWLOntology onto) throws FileNotFoundException, JWNLException {
		
		Map<String, Set<String>> hyponymMap = new HashMap<String, Set<String>>();
	
		Set<String> hyponyms = new HashSet<String>();
		
		for (OWLClass c : onto.getClassesInSignature()) {
			
			
			if (WordNet.containedInWordNet(c.getIRI().getFragment().toLowerCase())) {
        
        hyponyms = null;
        try{
          hyponyms = WordNet.getHyponymsAsSet(c.getIRI().getFragment().toLowerCase());
        } catch (Exception e){
          throw new IllegalStateException("This function doesn't work because of the above line. Makes some string tokenizer exception");
        }
				
				if (hyponyms != null) {
				
				hyponymMap.put(c.getIRI().getFragment().toLowerCase(), hyponyms);
								
				}
			}
			
		}

		return hyponymMap;
		
	}


}
