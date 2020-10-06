package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithms.alignmentcombination.AlignmentConflictResolution;
import algorithms.alignmentcombination.ProfileWeight;
import algorithms.alignmentcombination.ProfileWeightSubsumption;
import algorithms.ui.SemanticMatcher;
import algorithms.utilities.AlignmentOperations;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import services.enums.AlgorithmType;
import services.settings.AlgorithmSettings;

public class AlignmentCombiner {
    public URIAlignment combine(Map<AlgorithmType, List<URIAlignment>> results) throws Exception {
        Map<AlgorithmType, URIAlignment> alignmentCombinedMap = new HashMap<AlgorithmType, URIAlignment>();
        if (results.containsKey(AlgorithmType.Equivalence)) {
            List<URIAlignment> alignments = results.get(AlgorithmType.Equivalence);
            URIAlignment alignmentCombined = combineEQ(alignments);
            alignmentCombinedMap.put(AlgorithmType.Equivalence, alignmentCombined);
        }
        if(results.containsKey(AlgorithmType.Subsumption)){
            List<URIAlignment> alignments = results.get(AlgorithmType.Subsumption);
            URIAlignment alignmentCombined = combineSUB(alignments);
            alignmentCombinedMap.put(AlgorithmType.Subsumption, alignmentCombined);
        }
        if (alignmentCombinedMap.keySet().size() == 1){
            return alignmentCombinedMap.values().iterator().next(); //Første URIAlignment-en i hashmappen.
        }
        URIAlignment combinedEQ = alignmentCombinedMap.get(AlgorithmType.Equivalence);
        URIAlignment combinedSUB = alignmentCombinedMap.get(AlgorithmType.Subsumption);
        return combineEQandSUB(combinedEQ, combinedSUB);
    }

    URIAlignment combineEQ(List<URIAlignment> eqAlignments) throws Exception {
        String mismatchFile = AlgorithmSettings.MISMATCHFILE;
        URIAlignment alignmentCombinedWithMismatches = ProfileWeight.computeProfileWeightingEquivalence(eqAlignments);
        return SemanticMatcher.removeMismatches(alignmentCombinedWithMismatches, mismatchFile); //Vurdere å flytte funksjonen.
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
