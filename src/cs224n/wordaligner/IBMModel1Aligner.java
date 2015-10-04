package cs224n.wordaligner;

import cs224n.util.*;

import java.util.ArrayList;
import java.util.Arrays;
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
public class IBMModel1Aligner implements WordAligner {

	private static final long serialVersionUID = 1315751943476440515L;

	// TODO: Use arrays or Counters for collecting sufficient statistics
	// from the training data.
	private CounterMap<String, String> sourceTargetCounts;
	private CounterMap<String, String> transProb = new CounterMap<String, String>();
	private CounterMap<Integer, Integer> alignmentProb = new CounterMap<Integer, Integer>();
	private CounterMap<String, String> transCounts = new CounterMap<String, String>();

	public Alignment align(SentencePair sentencePair) {
		// Placeholder code below.
		// TODO Implement an inference algorithm for Eq.1 in the assignment
		// handout to predict alignments based on the counts you collected with
		// train().
		Alignment alignment = new Alignment();
		int currentMaxPositionJ = -1;
		double currentMaxProb = Double.MIN_VALUE;

		for (int i = 0; i < sentencePair.sourceWords.size(); i++) {
			String sourceTerm = sentencePair.sourceWords.get(i);
			for (int j = 0; j < sentencePair.targetWords.size(); j++) {
				String targeTerm = sentencePair.targetWords.get(j);
				if (transProb.getCount(sourceTerm, targeTerm) > currentMaxProb) {
					currentMaxProb = transCounts
							.getCount(sourceTerm, targeTerm);
					currentMaxPositionJ = j;
				}
			}
			alignment.addPredictedAlignment(currentMaxPositionJ, i);
			currentMaxProb = Double.MIN_VALUE;

		}
		// YOUR CODE HERE

		return alignment;
	}

	public void train(List<SentencePair> trainingPairs) {
		int maxNumOfRounds = 1000;
		// YOUR CODE HERE
		// Initialze
		for (SentencePair sentencePair: trainingPairs){
			for(String sourceTerm : sentencePair.sourceWords){
				for(String targetTerm : sentencePair.targetWords){
					transProb.setCount(sourceTerm, targetTerm, 1);
				}
			}
		}
		
		
		
		
		for (int t = 0; t < maxNumOfRounds; t++) {
			//
			if(t % 10 == 0)
				System.out.println(t/10 + " percent done...");
			for (SentencePair sentencePair : trainingPairs) {
				for (int i = 0; i < sentencePair.sourceWords.size(); i++) {
					for (int j = 0; j < sentencePair.targetWords.size(); j++) {
						alignmentProb
								.setCount(
										i,
										j,
										getAlignmentProb(i, j, sentencePair,
												transProb));
						transCounts.incrementCount(
								sentencePair.sourceWords.get(i),
								sentencePair.targetWords.get(j),
								alignmentProb.getCount(i, j));
					}

				}
			}
			updateTransProb(transProb, transCounts);
		}
	}

	private void updateTransProb(CounterMap<String, String> transProb,
			CounterMap<String, String> transCounts) {
		for (String sourceTerm : transCounts.keySet()) {
			for (String targetTerm : transCounts.getCounter(sourceTerm)
					.keySet()) {
				transProb.setCount(sourceTerm, targetTerm,
						transCounts.getCount(sourceTerm, targetTerm));
			}
		}
		transCounts = new CounterMap<String, String>();

		return;
	}

	private double getAlignmentProb(int i, int j, SentencePair sentencePair,
			CounterMap<String, String> transProb) {
		double numerator = transProb.getCount(sentencePair.sourceWords.get(i),
				sentencePair.targetWords.get(j));
		double denormator = transProb.getCounter(sentencePair.sourceWords.get(i)).totalCount();

		return numerator / denormator;
		
	}
	
	public static void main(String[] args){
		String[] sourceSent1 = new String[]{"A", "B"};
		String[] targetSent1 = new String[]{"a", "b"};
		String[] sourceSent2 = new String[]{"B", "C"};
		String[] targetSent2 = new String[]{"b", "c"};
		
		List<SentencePair> sentenceList = new ArrayList<SentencePair>();
		//SentencePair sentencePair = new SentencePair(0, "", )
		sentenceList.add(new SentencePair(0, "", Arrays.asList(sourceSent1), Arrays.asList(targetSent1)));
		sentenceList.add(new SentencePair(1, "", Arrays.asList(sourceSent2), Arrays.asList(targetSent2)));
		IBMModel1Aligner ibm1 = new IBMModel1Aligner();
		ibm1.train(sentenceList);
	}
}
