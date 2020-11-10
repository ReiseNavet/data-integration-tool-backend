package services;

import java.io.File;
import java.util.List;
import java.util.Map;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.enums.SemanticRelation;
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
      boolean useSubsumption, String baseSaveLocation) throws Exception {
    File sourceOntology = inputParser.parseInput(sourceFilePath, baseSaveLocation + "/source.owl");
    File targetOntology = inputParser.parseInput(targetFilePath, baseSaveLocation + "/target.owl");

    Map<SemanticRelation, List<Algorithm>> pickedAlgorithms = algorithmPicker.pickAlgorithms(sourceOntology, targetOntology, useEquivalence, useSubsumption);
    Map<SemanticRelation, List<URIAlignment>> alignments = algorithmRunner.run(sourceOntology, targetOntology, pickedAlgorithms);
    URIAlignment finalAlignment = alignmentCombiner.combine(alignments);

    return finalAlignment;
  }
}
