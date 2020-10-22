package services;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLOntology;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.IO.OWLOntologyToFile;
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
      boolean useSubsumption, String baseSaveLocation) throws Exception {

    OWLOntology sourceOntology = inputParser.parseInput(sourceFilePath);
    OWLOntology targetOntology = inputParser.parseInput(targetFilePath);

    File sourceFile = OWLOntologyToFile.Convert(sourceOntology, baseSaveLocation + "/source");
    File targetFile = OWLOntologyToFile.Convert(targetOntology, baseSaveLocation + "/target");

    Map<AlgorithmType, List<Algorithm>> pickedAlgorithms = algorithmPicker.pickAlgorithms(sourceFile, targetFile, useEquivalence, useSubsumption);
    Map<AlgorithmType, List<URIAlignment>> alignments = algorithmRunner.run(sourceFile, targetFile, pickedAlgorithms);
    URIAlignment finalAlignment = alignmentCombiner.combine(alignments);

    return finalAlignment;
  }
}
