package algorithms.evaluation.general;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.NotImplementedException;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;

import algorithms.utilities.StringUtilities;
import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.impl.eval.SemPRecEvaluator;
import fr.inrialpes.exmo.align.parser.AlignmentParser;



/**
 * A set of utility methods for evaluating alignments against a reference alignment. 
 * @author audunvennesland
 *
 */
public class Evaluator {
	
	public static void main(String[] args) throws AlignmentException, URISyntaxException {
		
		String referenceAlignmentFile = "./files/_PHD_EVALUATION/_EVALUATION_SYNPRECREC/ATMONTO-AIRM/ALIGNMENTS/PROFILEWEIGHT/SUBSUMPTION_SIGMOID/ALIGNMENTS/PROFILEWEIGHT_SUBSUMPTION_SIGMOID_0.0.rdf";
		String evaluatedAlignmentFile = "./files/_PHD_EVALUATION/_EVALUATION_SYNPRECREC/ATMONTO-AIRM/REFALIGN/ReferenceAlignment-ATMONTO-AIRM-SUBSUMPTION.rdf";
		
		AlignmentParser evaluatedAlignParser = new AlignmentParser(0);
		
		URIAlignment evaluatedAlignment = (URIAlignment) evaluatedAlignParser.parse(new URI(StringUtilities.convertToFileURL(evaluatedAlignmentFile)));
				
		evaluateSingleAlignment(evaluatedAlignment, referenceAlignmentFile);
	}
	
	/**
	 * Evaluates a single alignment against a reference alignment and prints precision, recall, f-measure, true positives (TP), false positives (FP) and false negatives (FN)
	 * @param inputAlignmentFileName
	 * @param referenceAlignmentFileName
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 */
	public static void evaluateSemanticPrecisionAndRecall (URIAlignment inputAlignment, String referenceAlignmentFileName) throws AlignmentException, URISyntaxException {

		AlignmentParser refAlignParser = new AlignmentParser(0);

		Alignment referenceAlignment = refAlignParser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignmentFileName)));

		Properties p = new Properties();
		//PRecEvaluator eval = new PRecEvaluator(referenceAlignment, inputAlignment);
		
		SemPRecEvaluator eval = new SemPRecEvaluator(referenceAlignment, inputAlignment);

		eval.eval(p);

		System.out.println("------------------------------");
		System.out.println("Evaluator scores for " + inputAlignment.getType());
		System.out.println("------------------------------");
		System.out.println("F-measure: " + eval.getResults().getProperty("fmeasure").toString());
		System.out.println("Precision: " + eval.getResults().getProperty("precision").toString());
		System.out.println("Recall: " + eval.getResults().getProperty("recall").toString());

		System.out.println("True positives (TP): " + eval.getResults().getProperty("true positive").toString());

		int fp = eval.getFound() - eval.getCorrect();
		System.out.println("False positives (FP): " + fp);
		int fn = eval.getExpected() - eval.getCorrect();
		System.out.println("False negatives (FN): " + fn);
		System.out.println("\n");

	}
	

	/**
	 * Evaluates a single alignment against a reference alignment and prints precision, recall, f-measure, true positives (TP), false positives (FP) and false negatives (FN)
	 * @param inputAlignmentFileName
	 * @param referenceAlignmentFileName
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 */
	public static void evaluateSingleAlignment (URIAlignment inputAlignment, String referenceAlignmentFileName) throws AlignmentException, URISyntaxException {

		AlignmentParser refAlignParser = new AlignmentParser(0);

		Alignment referenceAlignment = refAlignParser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignmentFileName)));
		
		EvaluationScore evalScore = ComputeSyntacticEvaluationScores.getSyntacticEvaluationScore(inputAlignment, referenceAlignment);

		System.out.println("------------------------------");
		System.out.println("Evaluator scores for " + inputAlignment.getType());
		System.out.println("------------------------------");
		System.out.println("F-measure: " + evalScore.getfMeasure());
		System.out.println("Precision: " + evalScore.getPrecision());
		System.out.println("Recall: " + evalScore.getRecall());
		
	}
	
	/**
	 * Evaluates a single alignment against a reference alignment and prints precision, recall, f-measure, true positives (TP), false positives (FP) and false negatives (FN) to both console and file.
	 * @param alignmentName a given name that will be printed in the evaluation summary
	 * @param inputAlignment the alignment to evaluate
	 * @param referenceAlignmentFileName the reference alignment the input alignment is evaluated against.
	 * @param output a path to a file where the evaluation summary is printed
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 * @throws IOException
	   Jul 16, 2019
	 */
	public static void evaluateSingleAlignment (String alignmentName, Alignment inputAlignment, String referenceAlignmentFileName, String output) throws AlignmentException, URISyntaxException, IOException {

		AlignmentParser refAlignParser = new AlignmentParser(0);

		Alignment referenceAlignment = refAlignParser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignmentFileName)));
		
		File outputFile = new File(output);
		
		PrintWriter writer = new PrintWriter(
				new BufferedWriter(
						new FileWriter(outputFile)), true); 

		Properties p = new Properties();
		PRecEvaluator eval = new PRecEvaluator(referenceAlignment, inputAlignment);
		

		eval.eval(p);
		int fp = eval.getFound() - eval.getCorrect();
		int fn = eval.getExpected() - eval.getCorrect();

		//print to file
		writer.println("-----------------------------------------------------------------------");
		writer.println("Evaluator scores for " + alignmentName);
		writer.println("-----------------------------------------------------------------------");
		writer.println("Number of relations: " + inputAlignment.nbCells());
		writer.println("-----------------------------------------------------------------------");
		writer.println("F-measure: " + eval.getResults().getProperty("fmeasure").toString());
		writer.println("Precision: " + eval.getResults().getProperty("precision").toString());
		writer.println("Recall: " + eval.getResults().getProperty("recall").toString());
		writer.println("True positives (TP): " + eval.getResults().getProperty("true positive").toString());		
		writer.println("False positives (FP): " + fp);		
		writer.println("False negatives (FN): " + fn);
		writer.println("\n");
		writer.flush();
		writer.close();
		
		//print to console
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("Evaluator scores for " + alignmentName);
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("Number of relations: " + inputAlignment.nbCells());
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("F-measure: " + eval.getResults().getProperty("fmeasure").toString());
		System.out.println("Precision: " + eval.getResults().getProperty("precision").toString());
		System.out.println("Recall: " + eval.getResults().getProperty("recall").toString());
		System.out.println("True positives (TP): " + eval.getResults().getProperty("true positive").toString());
		System.out.println("False positives (FP): " + fp);
		System.out.println("False negatives (FN): " + fn);
		System.out.println("\n");

	}
	
	/**
	 * Evaluates a single alignment against a reference alignment and prints precision, recall, f-measure, true positives (TP), false positives (FP) and false negatives (FN) to both console and file.
	 * @param alignmentName a given name that will be printed in the evaluation summary
	 * @param inputAlignment the URIAlignment to evaluate
	 * @param referenceAlignmentFileName the reference alignment the input alignment is evaluated against.
	 * @param output a path to a file where the evaluation summary is printed
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 * @throws IOException
	   Jul 16, 2019
	 */
	public static void evaluateSingleAlignment (String alignmentName, URIAlignment inputAlignment, String referenceAlignmentFileName, String output) throws AlignmentException, URISyntaxException, IOException {

		AlignmentParser refAlignParser = new AlignmentParser(0);

		Alignment referenceAlignment = refAlignParser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignmentFileName)));
		
		File outputFile = new File(output);
		
		PrintWriter writer = new PrintWriter(
				new BufferedWriter(
						new FileWriter(outputFile)), true); 

		Properties p = new Properties();
		PRecEvaluator eval = new PRecEvaluator(referenceAlignment, inputAlignment);

		eval.eval(p);
		int fp = eval.getFound() - eval.getCorrect();
		int fn = eval.getExpected() - eval.getCorrect();

		//print to file
		writer.println("-----------------------------------------------------------------------");
		writer.println("Evaluator scores for " + alignmentName);
		writer.println("-----------------------------------------------------------------------");
		writer.println("Number of relations: " + inputAlignment.nbCells());
		writer.println("-----------------------------------------------------------------------");
		writer.println("F-measure: " + eval.getResults().getProperty("fmeasure").toString());
		writer.println("Precision: " + eval.getResults().getProperty("precision").toString());
		writer.println("Recall: " + eval.getResults().getProperty("recall").toString());
		writer.println("True positives (TP): " + eval.getResults().getProperty("true positive").toString());		
		writer.println("False positives (FP): " + fp);		
		writer.println("False negatives (FN): " + fn);
		writer.println("\n");
		writer.flush();
		writer.close();
		
		//print to console
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("Evaluator scores for " + alignmentName);
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("Number of relations: " + inputAlignment.nbCells());
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("F-measure: " + eval.getResults().getProperty("fmeasure").toString());
		System.out.println("Precision: " + eval.getResults().getProperty("precision").toString());
		System.out.println("Recall: " + eval.getResults().getProperty("recall").toString());
		System.out.println("True positives (TP): " + eval.getResults().getProperty("true positive").toString());
		System.out.println("False positives (FP): " + fp);
		System.out.println("False negatives (FN): " + fn);
		System.out.println("\n");

	}
	
	/**
	 * Evaluates a single alignment against a reference alignment and prints precision, recall, f-measure, true positives (TP), false positives (FP) and false negatives (FN) to console only.
	 * @param inputAlignmentFileName
	 * @param referenceAlignmentFileName
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 * @throws IOException 
	 */
	public static void evaluateSingleAlignment (String alignmentName, URIAlignment inputAlignment, String referenceAlignmentFileName) throws AlignmentException, URISyntaxException, IOException {

		AlignmentParser refAlignParser = new AlignmentParser(0);

		Alignment referenceAlignment = refAlignParser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignmentFileName)));

		Properties p = new Properties();
		PRecEvaluator eval = new PRecEvaluator(referenceAlignment, inputAlignment);

		eval.eval(p);
		int fp = eval.getFound() - eval.getCorrect();
		int fn = eval.getExpected() - eval.getCorrect();
		
		//print to console
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("Evaluator scores for " + alignmentName);
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("Number of relations: " + inputAlignment.nbCells());
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("F-measure: " + eval.getResults().getProperty("fmeasure").toString());
		System.out.println("Precision: " + eval.getResults().getProperty("precision").toString());
		System.out.println("Recall: " + eval.getResults().getProperty("recall").toString());
		System.out.println("True positives (TP): " + eval.getResults().getProperty("true positive").toString());
		System.out.println("False positives (FP): " + fp);
		System.out.println("False negatives (FN): " + fn);
		System.out.println("\n");

	}
	
	/**
	 * Evaluates a single (basic) alignment against a reference alignment and prints precision, recall, f-measure, true positives (TP), false positives (FP) and false negatives (FN) to console.
	 * @param inputAlignmentFileName
	 * @param referenceAlignmentFileName
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 */
	public static void evaluateSingleAlignment (BasicAlignment inputAlignment, String referenceAlignmentFileName) throws AlignmentException, URISyntaxException {

		AlignmentParser refAlignParser = new AlignmentParser(0);

		Alignment referenceAlignment = refAlignParser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignmentFileName)));

		Properties p = new Properties();
		PRecEvaluator eval = new PRecEvaluator(referenceAlignment, inputAlignment);

		eval.eval(p);

		System.out.println("------------------------------");
		System.out.println("Evaluator scores for " + inputAlignment.getType());
		System.out.println("------------------------------");
		System.out.println("F-measure: " + eval.getResults().getProperty("fmeasure").toString());
		System.out.println("Precision: " + eval.getResults().getProperty("precision").toString());
		System.out.println("Recall: " + eval.getResults().getProperty("recall").toString());

		System.out.println("True positives (TP): " + eval.getResults().getProperty("true positive").toString());

		int fp = eval.getFound() - eval.getCorrect();
		System.out.println("False positives (FP): " + fp);
		int fn = eval.getExpected() - eval.getCorrect();
		System.out.println("False negatives (FN): " + fn);
		System.out.println("\n");

	}

	/**
	 * Evaluates all alignments in a folder against a reference alignment prints for each alignment: precision, recall, f-measure, true positives (TP), false positives (FP) and false negatives (FN)
	 * @param folderName The folder holding all alignments
	 * @param referenceAlignmentFileName
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 */
	public static void evaluateAlignmentFolder (String folderName, String referenceAlignmentFileName) throws AlignmentException, URISyntaxException {

		AlignmentParser aparser = new AlignmentParser(0);
		Alignment referenceAlignment = aparser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignmentFileName)));

		Properties p = new Properties();

		File folder = new File(folderName);
		File[] filesInDir = folder.listFiles();
		Alignment evaluatedAlignment = null;
		PRecEvaluator eval = null;

		for (int i = 0; i < filesInDir.length; i++) {

			String URI = StringUtilities.convertToFileURL(folderName) + "/" + StringUtilities.stripPath(filesInDir[i].toString());
			System.out.println("Evaluating file " + URI);
			evaluatedAlignment = aparser.parse(new URI(URI));

			eval = new PRecEvaluator(referenceAlignment, evaluatedAlignment);

			eval.eval(p);

			System.out.println("Number of relations in alignment: " + evaluatedAlignment.nbCells());

			System.out.println("------------------------------");
			System.out.println("Evaluator scores for " + StringUtilities.stripPath(filesInDir[i].toString()));
			System.out.println("------------------------------");
			System.out.println("F-measure: " + eval.getResults().getProperty("fmeasure").toString());
			System.out.println("Precision: " + eval.getResults().getProperty("precision").toString());
			System.out.println("Recall: " + eval.getResults().getProperty("recall").toString());

			System.out.println("True positives (TP): " + eval.getResults().getProperty("true positive").toString());

			int fp = eval.getFound() - eval.getCorrect();
			System.out.println("False positives (FP): " + fp);
			int fn = eval.getExpected() - eval.getCorrect();
			System.out.println("False negatives (FN): " + fn);
			System.out.println("\n");
		}
	}



	/**
	 * Produces a Map of key: matcher (i.e. alignment produced by a particular matcher) and value: F-measure score from evaluation of against the alignment for that particular matcher
	 * @param folderName The folder holding the alignments to be evaluated
	 * @param referenceAlignmentFileName
	 * @return A Map<String, Double) holding the matcher (alignment) and F-measure score for that particular matcher (alignment)
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 */
	public static Map<String, EvaluationScore> evaluateAlignmentFolderMap (String folderName, String referenceAlignmentFileName) throws AlignmentException, URISyntaxException {

		Map<String, EvaluationScore> evalFolderMap = new HashMap<String, EvaluationScore>();

		double precision = 0;
		double recall = 0;		
		double fMeasure = 0;

		AlignmentParser aparser = new AlignmentParser(0);
		Alignment referenceAlignment = aparser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignmentFileName)));

		Properties p = new Properties();
		File folder = new File(folderName);
		File[] filesInDir = folder.listFiles();

		Alignment evaluatedAlignment = null;
		PRecEvaluator eval = null;



		for (int i = 0; i < filesInDir.length; i++) {

			EvaluationScore evalScore = new EvaluationScore();

			String URI = StringUtilities.convertToFileURL(folderName) + "/" + StringUtilities.stripPath(filesInDir[i].toString());

			evaluatedAlignment = aparser.parse(new URI(URI));

			eval = new PRecEvaluator(referenceAlignment, evaluatedAlignment);

			eval.eval(p);

			precision = Double.valueOf(eval.getResults().getProperty("precision").toString());
			recall = Double.valueOf(eval.getResults().getProperty("recall").toString());
			fMeasure = Double.valueOf(eval.getResults().getProperty("fmeasure").toString());

			evalScore.setPrecision(precision);
			evalScore.setRecall(recall);
			evalScore.setfMeasure(fMeasure);

			evalFolderMap.put(URI.substring(URI.lastIndexOf("/") +1), evalScore);

		}

		return evalFolderMap;

	}

	/**
	 * Runs a complete evaluation producing F-measure scores for individual matchers and combination strategies. The F-measure scores are printed to console.
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 * @throws FileNotFoundException 
	 */
	public static void runCompleteEvaluation (String alignmentsFolder, String referenceAlignment, String outputPath, String datasetName) throws AlignmentException, URISyntaxException, FileNotFoundException {
		throw new NotImplementedException("Look at earlier versions for function body. It was almost completely unused");
	}

	
	/**
	 * Evaluates a single matcher at different thresholds and prints the evaluation summary and a chart to Excel.
	 * @param evaluationMap a map holding the confidence threshold as key and an evaluation score object (including precision, recall and f-measure) as value.
	 * @param output the excel file presenting the evaluation summary along with a representative chart.
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 * @throws IOException
	   Jul 16, 2019
	 */
	public static void evaluateSingleMatcherThresholds (Map<String, EvaluationScore> evaluationMap, String output) throws AlignmentException, URISyntaxException, IOException {
		throw new NotImplementedException("Look at earlier versions for function body. It was almost completely unused");
	}

}






