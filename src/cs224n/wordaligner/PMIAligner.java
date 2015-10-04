package cs224n.wordaligner;

import cs224n.util.*;
import java.util.List;

/**
 * Simple word alignment baseline model that maps source positions to target
 * positions along the diagonal of the alignment grid.
 * 
 * IMPORTANT: Make sure that you read the comments in the
 * cs224n.wordaligner.WordAligner interface.
 * 
 * @author Dan Klein
 * @author Spence Green
 */
public class PMIAligner implements WordAligner {

	private static final long serialVersionUID = 1315751943476440515L;

	// TODO: Use arrays or Counters for collecting sufficient statistics
	// from the training data.

	private CounterMap<String, String> SiTjCounts = new CounterMap<String, String>();
	private Counter<String> SiCounts = new Counter<String>();
	private Counter<String> TjCounts = new Counter<String>();

	public Alignment align(SentencePair sentencePair) {
		// Placeholder code below.
		// TODO Implement an inference algorithm for Eq.1 in the assignment
		// handout to predict alignments based on the counts you collected with
		// train().
		Alignment alignment = new Alignment();
		// YOUR CODE HERE
		double currentMaxPossibility = Double.MIN_VALUE;
		double currentPossibility = SiCounts.totalCount()
				* TjCounts.totalCount() / SiTjCounts.totalCount();
		int currentMaxPositionJ = -1;
		String sourceTerm;
		String targetTerm;
		for (int i = 0; i < sentencePair.sourceWords.size(); i++) {
			sourceTerm = sentencePair.sourceWords.get(i);
			for (int j = 0; j < sentencePair.targetWords.size(); j++) {
				targetTerm = sentencePair.targetWords.get(j);

				currentPossibility *= SiTjCounts.getCount(sourceTerm,
						targetTerm);
				currentPossibility /= (SiCounts.getCount(sourceTerm) * TjCounts
						.getCount(targetTerm));
				if (currentPossibility > currentMaxPossibility) {
					currentMaxPossibility = currentPossibility;
					currentMaxPositionJ = j;
				}
				currentPossibility = SiCounts.totalCount()
						* TjCounts.totalCount() / SiTjCounts.totalCount();
			}

			alignment.addPredictedAlignment(currentMaxPositionJ, i);

			currentMaxPossibility = Double.MIN_VALUE;

		}
		return alignment;
	}

	public void train(List<SentencePair> trainingPairs) {

		// YOUR CODE HERE
		for (SentencePair sentencePair : trainingPairs) {
			for (String sourceTerm : sentencePair.sourceWords) {
				SiCounts.incrementCount(sourceTerm, 1);
				for (String targetTerm : sentencePair.targetWords) {
					SiTjCounts.incrementCount(sourceTerm, targetTerm, 1);
				}
			}

			for (String targetTerm : sentencePair.targetWords) {
				TjCounts.incrementCount(targetTerm, 1);
			}
		}

	}
}
