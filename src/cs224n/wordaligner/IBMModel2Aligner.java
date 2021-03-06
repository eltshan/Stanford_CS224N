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
public class IBMModel2Aligner extends IBMModel1Aligner {

  private static final long serialVersionUID = 1315751943476440515L;
  
  // TODO: Use arrays or Counters for collecting sufficient statistics
  // from the training data.
  private CounterMap<String,String> transCounts = new CounterMap<String, String>();
  private CounterMap<String,Integer> alignCounts = new CounterMap<String,Integer>(); 
  private CounterMap<String,Integer> alignmentProb = new CounterMap<String,Integer>();
  int maxNumOfRounds = 10;
  public Alignment align(SentencePair sentencePair) {
    // Placeholder code below. 
    // TODO Implement an inference algorithm for Eq.1 in the assignment
    // handout to predict alignments based on the counts you collected with train().
    Alignment alignment = new Alignment();
    
    // YOUR CODE HERE
   
    return alignment;
  }

  public void train(List<SentencePair> trainingPairs) {
    super.train(trainingPairs);
    
    //initialize everything
    for (SentencePair sentencePair : trainingPairs){
    for(int i = 0; i < sentencePair.targetWords.size(); i++){
    	for(int j = 0;j < sentencePair.sourceWords.size(); j++){
		int l = sentencePair.targetWords.get(i).length();
		int m = sentencePair.sourceWords.get(j).length();
		String triple = "" + i + "+" + l + "+" + m;
		alignmentProb.setCount(triple, j, 1);
	}
    }
    }
    //
    //
    //
    for(int t = 0; t < maxNumOfRounds; t++){
    	System.out.println(t + "out of " + maxNumOfRounds + " iterations done...");
	for(SentencePair sentencePair : trainingPairs) {
		for( int i = 0; i < sentencePair.targetWords.size(); i++){
			for(int j = 0; j < sentencePair.sourceWords.size(); j++){
				updateCounts(i, j, sentencePair);
			}
		}
	}
    }
    updateTransProb(super.transProb, super.transCounts);
    updateAlignProb();
    // YOUR CODE HERE

  }
  
  private void updateAlignProb(){
  	for (String tmp : alignmentProb.keySet()){
		for (Integer j : alignmentProb.getCounter(tmp).keySet()){
			alignmentProb.setCount(tmp, j, alignCounts.getCount(tmp,j)/
					alignCounts.getCounter(tmp).totalCount());
		}	
	}
  }

  private void updateCounts(int i, int j, SentencePair sentencePair){
	  int l = sentencePair.targetWords.size();
	  int m = sentencePair.sourceWords.size();
	  String tripple = "" + i + "+" + l + "+" + m;
	  double updateNum = updateHelper(i,j,tripple,sentencePair);
	  alignCounts.incrementCount(tripple, j, updateNum);
	  transCounts.incrementCount(sentencePair.targetWords.get(i),
			  	     sentencePair.sourceWords.get(j), updateNum);
  }

  private double updateHelper(int i, int j, String triple,
		  SentencePair sentencePair){
	  double numerator = transProb.getCount(sentencePair.targetWords.get(i),
			  			sentencePair.sourceWords.get(j));
	  double denormator = 0;
	  for(int k = 0; k < sentencePair.sourceWords.size(); k++){
	  	denormator += alignmentProb.getCount(triple, k)*
			super.transProb.getCount(sentencePair.targetWords.get(i),
						 sentencePair.sourceWords.get(k));
	  }
	  return numerator / denormator;
  }

}
