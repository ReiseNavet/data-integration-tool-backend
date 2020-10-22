package algorithms.utilities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Different variants of the Jaccard set similarity
 * @author audunvennesland
 *
 */
public class SimilarityMetrics {

    public static void main(String[] args) {
		Set<String> set1 = new HashSet<String>(Arrays.asList(new String[]{"hi", "hei"}));
		Set<String> set2 = new HashSet<String>(Arrays.asList(new String[]{"hei", "hey"}));
		System.out.println(jaccardSetSim(set1, set2));
    }

	/**
	 * Computes the Jaccard similarity between two sets of strings
	 * @param set1 the first set of strings
	 * @param set2 the second set of strings
	 * @return the jaccard similarity score (intersection over union)
	   Jul 17, 2019
	 */
	public static double jaccardSetSim (Set<String> set1, Set<String> set2) {
		int size1 = set1.size();
		int size2 = set2.size();
		Set<String> unionSet = new HashSet<String>(set1);
		unionSet.addAll(set2);
		int union = unionSet.size();
		int intersection = size1 + size2 - union;
		double jaccardSetSim = (double) intersection / (double) union;
		return jaccardSetSim;
	}


	/**
	 * Computes a similarity score using a combination of Jaccard and ISUB
	 * @param confidence a threshold used to determine if two concepts are equal according to ISUB.
	 * @param concept1 represents an ontology concept
	 * @param concept2 represents a second ontology concept
	 * @param set1 represent a set of values associated with concept1 (e.g. set of properties associated with concept1)
	 * @param set2 represent a set of values associated with concept2 (e.g. set of properties associated with concept2)
	 * @return a similarity score computed using a combination of Jaccard and ISUB.
	   Jul 18, 2019
	 */
	public static double jaccardSetSimISubEqualConcepts (double confidence, String concept1, String concept2, Set<String> set1, Set<String> set2) {

		ISub isubMatcher = new ISub();

		int intersection = 0;
		int refinedIntersection = 0;
		int refinedUnion = 0;
		double isubScore = 0;

		for (String s1 : set1) {
			for (String s2 : set2) {
				//using ISub to compute a similarity score
				isubScore = isubMatcher.score(s1,s2);
				if (isubScore > confidence) {
					intersection += 1;
				}
			}
		}

		int union = (set1.size() + set2.size()) - intersection;

		if (set1.contains(concept2.toLowerCase()) && set2.contains(concept1.toLowerCase())) {
			refinedIntersection = intersection +2;
			refinedUnion = union - 2;
		} else if (set1.contains(concept2.toLowerCase()) || set2.contains(concept1.toLowerCase())) {
			refinedIntersection = intersection +1;
			refinedUnion = union - 1;
		} else {
			refinedIntersection = intersection;
			refinedUnion = union;
		}

		double jaccardSetSim = (double) refinedIntersection / (double) refinedUnion;

		return jaccardSetSim;
	}


}
