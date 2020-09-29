package services;

import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.lang.NotImplementedException;

import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.interfaces.Algorithm;

public class Manager {
  AlgorithmPicker algorithmPicker;
  AlgorithmRunner algorithmRunner;
  AlignmentCombiner alignmentCombiner;

  public Manager() {
    algorithmPicker = new AlgorithmPicker();
    algorithmRunner = new AlgorithmRunner();
    alignmentCombiner = new AlignmentCombiner();
  }


  public Object handle (String sourceFilePath, String targetFilePath, boolean useEquivalence, boolean useSubsumption) {

    Algorithm[] pickedAlgorithms = algorithmPicker.pickAlg(sourceFilePath, targetFilePath, useEquivalence, useSubsumption);

    URIAlignment[] alignments = algorithmRunner.run(sourceFilePath, targetFilePath, pickedAlgorithms);
    
    URIAlignment finalAlignment = alignmentCombiner.combine(alignments);

    return finalAlignment;
  }
}
