package algorithms.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicConfidence;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.impl.rel.A5AlgebraRelation;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import services.parsers.CellParser;

/**
 * @author audunvennesland
 * 19. aug. 2017 
 * 
 * Deleted methods:
 * createSubsumptionReferenceAlignment(BasicAlignment)
 * transformSMatchAlignment(File, File)
 * createAllRelations(File, File)
 * convertFromComaToAlignmentAPI(String, OWLOntology, OWLOntology)
 * convertToComaFormat(File, File, File)
 * fixEntityOrder(BasicAlignment)
 */
public class AlignmentOperations {
	
	public static void main(String[] args) throws OWLOntologyCreationException, AlignmentException, IOException, URISyntaxException {	

	String alignmentPath = "./files/_PHD_EVALUATION/_EVALUATION_SEMPREC_REC/ATMONTO-AIRM/AML/input/ATM_AML_Automatic_040820.rdf";
	String outputPath = "./files/_PHD_EVALUATION/_EVALUATION_SEMPREC_REC/ATMONTO-AIRM/AML/output/";
	String ontoFile1 = "/Users/audunvennesland/ontologies/ATM/ATMOntoCoreMerged.owl";
	String ontoFile2 = "/Users/audunvennesland/ontologies/ATM/airm-mono.owl";
	
	extractAllConfidenceThresholds(alignmentPath, outputPath, ontoFile1, ontoFile2);
	
	String candidateAlignment = "./files/_PHD_EVALUATION/_EVALUATION_SEMPREC_REC/BIBFRAME-SCHEMAORG/AML/output/bibframe-schema-org_AML_0.0.rdf";
	String referenceAlignment = "./files/_PHD_EVALUATION/_EVALUATION_SEMPREC_REC/BIBFRAME-SCHEMAORG/REFALIGN/ReferenceAlignment-BIBFRAME-SCHEMAORG-EQ-SUB.rdf";
	
	AlignmentParser aparser = new AlignmentParser(0);
	Alignment refalign =  aparser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignment)));
	Alignment candAlign =  aparser.parse(new URI(StringUtilities.convertToFileURL(candidateAlignment)));
	Alignment extractedAlignment = extractEqualRelations(candAlign, refalign);
	
	System.out.println("extractedAlignment contains " + extractedAlignment.nbCells());
	
	}
	/**
	 * Returns the URIAlignment converted to a JSON string
	 */
	public static String convertToJSON(URIAlignment alignment){
		String json = "[";
		for (Cell cell: alignment.getArrayElements()) {
			json += String.format("{\"source\": \"%s\", \"target\": \"%s\", \"relation\": \"%s\", \"confidence\": %s},", 
					CellParser.getSource(cell), CellParser.getTarget(cell), CellParser.getRelation(cell), CellParser.getConfidence(cell));
		}
		if (json.length() > 1){
			//Incase there are no cells, dont remove the "["
			json = json.substring(0, json.length() - 1);
		}
		json += "]";
		return json;
	}
	
	/**
	 * Returns an EvaluationScore object that measures the precision, recall and F-measure of a candidate alignment against a reference alignment. The method considers the type of relation.
	 * @param candidateAlignment the alignment being evaluated
	 * @param referenceAlignment the alignment holding the correct set of relations.
	 * @return EvaluationScore
	 * @throws AlignmentException
	   Aug 13, 2020
	 */
	public static Alignment extractEqualRelations (Alignment candidateAlignment, Alignment referenceAlignment) throws AlignmentException {
		
		
		Alignment thisAlignment = new BasicAlignment();
		
		for (Cell rac : referenceAlignment) {
			for (Cell ac : candidateAlignment) {
				
				if (rac.getObject1AsURI().equals(ac.getObject1AsURI()) 
						&& rac.getObject2AsURI().equals(ac.getObject2AsURI()) 
						&& rac.getRelation().getRelation().equals(ac.getRelation().getRelation())) {
					thisAlignment.addAlignCell(ac.getObject1(), ac.getObject2(), ac.getRelation().getRelation(), ac.getStrength());
				}
				
			}
		}
		
		return thisAlignment;
				
	

	}
	
	public static void extractAllConfidenceThresholds (String alignmentPath, String storePath, String ontoFile1, String ontoFile2) throws AlignmentException, IOException, OWLOntologyCreationException {
		
		
		String matcher = "AML";
		
		AlignmentParser parser = new AlignmentParser();		
		URIAlignment alignment = (URIAlignment) parser.parse(new File(alignmentPath).toURI().toString());
		
		double[] confidence = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
				
		AlignmentVisitor renderer = null; 
		File outputAlignment = null;
		PrintWriter writer = null;
		String alignmentFileName = null;
		BasicAlignment cutAlignment = null;
		BasicAlignment clonedAlignment = null;
		URIAlignment tempAlignment = null;
		
		System.out.println("The initial alignment contains " + alignment.nbCells() + " relations");
		
		for (double conf : confidence) {
			cutAlignment = (BasicAlignment)(alignment.clone());
			cutAlignment.toURIAlignment();
			cutAlignment.cut(conf);
			
			System.out.println("The cut alignment at confidence " + conf + " contains " + cutAlignment.nbCells() + " relations.");
			alignmentFileName = storePath + "/" + StringUtilities.stripOntologyName(ontoFile1.toString()) + 
					"-" + StringUtilities.stripOntologyName(ontoFile2.toString()) + "_" + matcher + "_"+ conf + ".rdf";
			
			outputAlignment = new File(alignmentFileName);
			
			writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(outputAlignment)), true); 
			renderer = new RDFRendererVisitor(writer);
			
			clonedAlignment = (BasicAlignment)(cutAlignment.clone());
			
			System.out.println("The cloned alignment at confidence " + conf + " contains " + clonedAlignment.nbCells() + " relations.");
			
			
			tempAlignment = clonedAlignment.toURIAlignment();
			
			System.out.println("The temp alignment at confidence " + conf + " contains " + tempAlignment.nbCells() + " relations.");
			
			//tempAlignment.init( onto1.getOntologyID().getOntologyIRI().toURI(), onto2.getOntologyID().getOntologyIRI().toURI(), A5AlgebraRelation.class, BasicConfidence.class );
			tempAlignment.render(renderer);
			
			writer.flush();
			writer.close();
			

		}
	}
	
	
	/**
	 * Removes relations from an alignment having a confidence of 0.0
	 * @param inputAlignment
	 * @return URIAlignment without relations having a confidence of 0.0
	 * @throws AlignmentException
	   Jul 17, 2019
	 */
	public static URIAlignment removeZeroConfidenceRelations(BasicAlignment inputAlignment) throws AlignmentException {
	
		URIAlignment alignmentWithNonZeroRelations = new URIAlignment();
		
		//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
		URI onto1URI = inputAlignment.getOntology1URI();
		URI onto2URI = inputAlignment.getOntology2URI();
		
		
		alignmentWithNonZeroRelations.init( onto1URI, onto2URI, A5AlgebraRelation.class, BasicConfidence.class );
		
		
		for (Cell c : inputAlignment) {
			if (c.getStrength() != 0.0) {
				
				alignmentWithNonZeroRelations.addAlignCell(c.getId(), c.getObject1AsURI(), c.getObject2AsURI(), c.getRelation().getRelation(), c.getStrength());
				
			} 
		}
		
		return alignmentWithNonZeroRelations;

	}
	
	/**
	 * Sorts an alignment by confidence value (in descending order)
	 * @param alignment an input alignment
	 * @return a list of cells (relations)
	 * @throws AlignmentException
	   Jul 17, 2019
	 */
	public static List<Cell> sortAlignment(URIAlignment alignment) throws AlignmentException {

		Map<Cell, Double> alignmentMap = new HashMap<Cell, Double>();
		
		for (Cell c : alignment) {
			alignmentMap.put(c, c.getStrength());
		}
		
		Map<Cell, Double> sortedAlignmentMap = sortByValues(alignmentMap);
		
		List<Cell> sortedList = new LinkedList<Cell>();

		for (Entry<Cell, Double> e : sortedAlignmentMap.entrySet()) {
			sortedList.add(e.getKey());
		}
		
		
		return sortedList;
		
		
	}
	
	
	/**
	 * Combines the relations from all alignments in a folder into a single alignment, basically the union. 
	 * @param folderName
	 * @return URIAlignment holding all relations from a set of individual alignments
	 * @throws AlignmentException
	   Feb 12, 2019
	 */
	public static URIAlignment combineEQAndSUBAlignments(URIAlignment eqAlignment, URIAlignment subAlignment) throws AlignmentException {
		URIAlignment combinedAlignment = new URIAlignment();

		
		for (Cell eqCell : eqAlignment) {
			combinedAlignment.addAlignCell(eqCell.getId(), eqCell.getObject1(), eqCell.getObject2(),  eqCell.getRelation().getRelation(), eqCell.getStrength());
		}
		
		for (Cell subCell : subAlignment) {
			//need to transform the less-than symbol '<' to &lt;
			if (subCell.getRelation().getRelation().equals("<")) {
				combinedAlignment.addAlignCell(subCell.getId(), subCell.getObject1(), subCell.getObject2(),  "&lt;", subCell.getStrength());
			} else {
			combinedAlignment.addAlignCell(subCell.getId(), subCell.getObject1(), subCell.getObject2(),  subCell.getRelation().getRelation(), subCell.getStrength());
			}
		}
		
		//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
		URI onto1URI = eqAlignment.getOntology1URI();
		URI onto2URI = eqAlignment.getOntology2URI();
		
		combinedAlignment.init( onto1URI, onto2URI, A5AlgebraRelation.class, BasicConfidence.class );
		
		return combinedAlignment;
	}
	
	/**
	 * Extracts all subsumption relations from an alignment
	 * @param inputAlignment the input alignment from which subsumption relations will be extracted.
	 * @return an URIAlignment holding only subsumption relations
	 * @throws AlignmentException
	 * @throws IOException
	   Jul 17, 2019
	 */
	public static URIAlignment extractSubsumptionRelations(URIAlignment inputAlignment) throws AlignmentException, IOException {
		URIAlignment subsumptionAlignment = new URIAlignment();
				
		URI onto1URI = inputAlignment.getOntology1URI();
		URI onto2URI = inputAlignment.getOntology2URI();
		
		//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
		subsumptionAlignment.init( onto1URI, onto2URI, A5AlgebraRelation.class, BasicConfidence.class );
		
		for (Cell c : inputAlignment) {
			if (!c.getRelation().getRelation().equals("=")) {
				subsumptionAlignment.addAlignCell(c.getObject1(), c.getObject2(), c.getRelation().getRelation(), c.getStrength());
			}
		}
		
		return subsumptionAlignment;
		
	}

	
	/**
	 * Extracts all equivalence relations from an alignment
	 * @param inputAlignment the input alignment from which equivalence relations will be extracted
	 * @return an URIAlignment holding only equivalence relations
	 * @throws AlignmentException
	 * @throws IOException
	   Jul 17, 2019
	 */
	public static URIAlignment extractEquivalenceRelations(URIAlignment inputAlignment) throws AlignmentException, IOException {
		
		URIAlignment equivalenceAlignment = new URIAlignment();
		
		URI onto1URI = inputAlignment.getOntology1URI();
		URI onto2URI = inputAlignment.getOntology2URI();
		
		//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
		equivalenceAlignment.init( onto1URI, onto2URI, A5AlgebraRelation.class, BasicConfidence.class );
		
		for (Cell c : inputAlignment) {
			if (c.getRelation().getRelation().equals("=")) {
				equivalenceAlignment.addAlignCell(c.getObject1(), c.getObject2(), c.getRelation().getRelation(), c.getStrength());
				System.out.println("Extracting " + c.getObject1() + " " + c.getObject2() + " " +  c.getRelation().getRelation() + " " +  c.getStrength());
			} 
		}
		
		return equivalenceAlignment;
	
	}
	
	
	

	/**
	 * This method normalizes (or scales) the confidence given to the relations between [0..1]. 
	 * @param initialAlignment an input alignment holding relations where the confidence values are to be normalized.
	 * @throws AlignmentException
	 */
	public static void normalizeConfidence (BasicAlignment initialAlignment) throws AlignmentException {

		URI onto1URI = initialAlignment.getOntology1URI();
		URI onto2URI = initialAlignment.getOntology2URI();

		BasicAlignment newAlignment = new BasicAlignment();


		//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
		newAlignment.init( onto1URI, onto2URI, A5AlgebraRelation.class, BasicConfidence.class );

		Set<Double> confidenceValues = new HashSet<Double>();
		for (Cell c : initialAlignment) {
			confidenceValues.add(c.getStrength());
		}

		//get min value in dataset (A)
		double min = Collections.min(confidenceValues);

		//get max value in dataset (B)
		double max = Collections.max(confidenceValues);

		//set min value (a) in the normalized scale (i.e. 0)
		double normMin = 0;

		//set max value (b) in the normalized scale (i.e. 1.0)
		double normMax = 1.0;

		double thisConfidence = 0;		

		for (Cell cell : initialAlignment) {

			thisConfidence = normMin + (cell.getStrength()-min)*(normMax-normMin) / (max-normMin);
			cell.setStrength(thisConfidence);

		}


	}

	

	/**
	 * Compares two alignments and returns an alignment with the diff set of relations in these two alignments.
	 * @param alignment1 input alignment 1
	 * @param alignment2 input alignment 2
	 * @return a BasicAlignment holding the relations that differ from input alignments 1 and 2
	 * @throws AlignmentException
	   Jul 17, 2019
	 */
	public static BasicAlignment createDiffAlignment(BasicAlignment alignment1, BasicAlignment alignment2) throws AlignmentException {

		BasicAlignment diffAlignment = new BasicAlignment();

		diffAlignment = (BasicAlignment) alignment1.diff(alignment2);

		return diffAlignment;

	}
	
	/**
	 * Sorts a map by its values in descending order.
	 * @param map an input map to be sorted
	 * @return a Map object with sorted values.
	   Jul 17, 2019
	 */
	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
	    Comparator<K> valueComparator =  new Comparator<K>() {
	        public int compare(K k1, K k2) {
	            int compare = map.get(k2).compareTo(map.get(k1));
	            if (compare == 0) return 1;
	            else return compare;
	        }
	    };
	    Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
	    sortedByValues.putAll(map);
	    return sortedByValues;
	}
}
