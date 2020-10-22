package services;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLOntology;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.enums.AlgorithmType;
import services.interfaces.Algorithm;

public class Manager {
  AlgorithmPicker algorithmPicker;
  AlgorithmRunner algorithmRunner;
  AlignmentCombiner alignmentCombiner;
  InputParser inputParser;

  public Manager() {
    algorithmPicker = new AlgorithmPicker();
    algorithmRunner = new AlgorithmRunner();
    alignmentCombiner = new AlignmentCombiner();
    inputParser = new InputParser();
  }

  public URIAlignment handle(String sourceFilePath, String targetFilePath, boolean useEquivalence,
      boolean useSubsumption) throws Exception {

    File sourceFile = new File(sourceFilePath);
    File targetFile = new File(targetFilePath);

    OWLOntology sourceOntology = inputParser.parseInput(sourceFilePath);
    OWLOntology targetOntology = inputParser.parseInput(targetFilePath);

    Map<AlgorithmType, List<Algorithm>> pickedAlgorithms = algorithmPicker.pickAlgorithms(sourceFile, targetFile, useEquivalence, useSubsumption);
    Map<AlgorithmType, List<URIAlignment>> alignments = algorithmRunner.run(sourceFile, targetFile, pickedAlgorithms);
    URIAlignment finalAlignment = alignmentCombiner.combine(alignments);

    return finalAlignment;
  }
}
