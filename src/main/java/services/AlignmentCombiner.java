package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithms.alignmentcombination.AlignmentConflictResolution;
import algorithms.alignmentcombination.ProfileWeight;
import algorithms.alignmentcombination.ProfileWeightSubsumption;
import algorithms.utilities.AlignmentOperations;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.enums.SemanticRelation;

public class AlignmentCombiner {
  /**
   * Combine multiple alignment results into one result.
   */
  public URIAlignment combine(Map<SemanticRelation, List<URIAlignment>> results) throws Exception {
    Map<SemanticRelation, URIAlignment> alignmentCombinedMap = new HashMap<SemanticRelation, URIAlignment>();
    if (results.containsKey(SemanticRelation.Equivalence)) {
      List<URIAlignment> alignments = results.get(SemanticRelation.Equivalence);
      URIAlignment alignmentCombined = combineEQ(alignments);
      alignmentCombinedMap.put(SemanticRelation.Equivalence, alignmentCombined);
    }
    if(results.containsKey(SemanticRelation.Subsumption)){
      List<URIAlignment> alignments = results.get(SemanticRelation.Subsumption);
      URIAlignment alignmentCombined = combineSUB(alignments);
      alignmentCombinedMap.put(SemanticRelation.Subsumption, alignmentCombined);
    }
    if (alignmentCombinedMap.keySet().size() == 1){
      return alignmentCombinedMap.values().iterator().next(); //Første URIAlignment-en i hashmappen.
    }
    URIAlignment combinedEQ = alignmentCombinedMap.get(SemanticRelation.Equivalence);
    URIAlignment combinedSUB = alignmentCombinedMap.get(SemanticRelation.Subsumption);
    return combineEQandSUB(combinedEQ, combinedSUB);
  }

  URIAlignment combineEQ(List<URIAlignment> eqAlignments) throws Exception {
    URIAlignment alignmentCombinedWithMismatches = ProfileWeight.computeProfileWeightingEquivalence(eqAlignments);
    return AlignmentConflictResolution.removeMismatches(alignmentCombinedWithMismatches);
  }

  URIAlignment combineSUB(List<URIAlignment> subAlignments) throws Exception {
    URIAlignment alignmentCombinedWithConflict = ProfileWeightSubsumption.computeProfileWeightingSubsumption(subAlignments);
    return AlignmentConflictResolution.resolveAlignmentConflict(alignmentCombinedWithConflict);
  }

  URIAlignment combineEQandSUB(URIAlignment combinedEQ, URIAlignment combinedSUB) throws Exception {
    URIAlignment mergedEQAndSubAlignment = AlignmentOperations.combineEQAndSUBAlignments(combinedEQ, combinedSUB);
    return AlignmentConflictResolution.resolveAlignmentConflict(mergedEQAndSubAlignment);
  }
}
