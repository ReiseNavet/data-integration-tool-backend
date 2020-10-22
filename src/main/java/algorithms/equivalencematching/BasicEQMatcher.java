package algorithms.equivalencematching;

import java.io.File;
import java.util.Properties;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import algorithms.utilities.ISub;
import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicConfidence;
import fr.inrialpes.exmo.align.impl.ObjectAlignment;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.impl.rel.A5AlgebraRelation;
import services.interfaces.Algorithm;

/**
 * This is a basic string matcher based on ISUB, see:
 * Stoilos, Giorgos, Giorgos Stamou, and Stefanos Kollias. "A string metric for ontology alignment." International Semantic Web Conference. Springer, Berlin, Heidelberg, 2005.
 * @author audunvennesland
 *
 */
public class BasicEQMatcher extends ObjectAlignment implements AlignmentProcess, Algorithm {

	public URIAlignment run(File ontoFile1, File ontoFile2) throws OWLOntologyCreationException, AlignmentException {
		return returnBasicEQMatcherAlignment(ontoFile1, ontoFile2);
	}

	/**
	 * Creates an alignment that on the basis of class and property similarity obtains a similarity score assigned to each relation in the alignment.
	 * A combination of Jaccard set similarity and the ISUB string similarity measure is used to compute the similarity score.
	 */
	public void align(Alignment alignment, Properties param) throws AlignmentException {

		double sim = 0;

		int idCounter = 0;

		try {
			for ( Object sourceObject: ontology1().getClasses() ){
				for ( Object targetObject: ontology2().getClasses() ){

					idCounter++;

					String source = ontology1().getEntityName(sourceObject).toLowerCase();
					String target = ontology2().getEntityName(targetObject).toLowerCase();

					//compute similarity between concepts using ISUB
					sim = computeISUBSim(source, target);
					addAlignCell("BasicStringMatcher" + idCounter + "_", sourceObject,targetObject, "=", sim );  

				}
			}

		} catch (Exception e) { e.printStackTrace(); }
	}

	public static double computeISUBSim (String concept1, String concept2) {
		
		ISub isubMatcher = new ISub();
		
		return isubMatcher.score(concept1.toLowerCase(), concept2.toLowerCase());
		
	}
	
	
	/**
	 * Returns an alignment holding relations computed by the Property Equivalence Matcher (PEM).
	 * @param ontoFile1 source ontology
	 * @param ontoFile2 target ontology
	 * @param weight a weight imposed on the confidence value (default 1.0)
	 * @return an URIAlignment holding relations (cells)
	 * @throws OWLOntologyCreationException
	 * @throws AlignmentException
	   Jul 15, 2019
	 */
	public static URIAlignment returnBasicEQMatcherAlignment (File ontoFile1, File ontoFile2) throws OWLOntologyCreationException, AlignmentException {


		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);

		AlignmentProcess a = new BasicEQMatcher();
		a.init(ontoFile1.toURI(), ontoFile2.toURI());
		Properties params = new Properties();
		params.setProperty("", "");
		a.align((Alignment)null, params);	
		
		BasicAlignment basicStringMatcherAlignment = (BasicAlignment) (a.clone());
		basicStringMatcherAlignment.normalise();

		URIAlignment BMSMAlignment = basicStringMatcherAlignment.toURIAlignment();
		BMSMAlignment.init( onto1.getOntologyID().getOntologyIRI().toURI(), onto2.getOntologyID().getOntologyIRI().toURI(), A5AlgebraRelation.class, BasicConfidence.class );

		return BMSMAlignment;

	}

}



