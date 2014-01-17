package eu.interedition.collatex.dekker.decision_tree2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import eu.interedition.collatex.AbstractTest;
import eu.interedition.collatex.simple.SimpleWitness;

/*
 * Test the alternatives in the decision tree
 * and the cost function.
 * 
 * @author: Ronald Haentjens Dekker
 */
public class DecisionTreeTest extends AbstractTest {
  
  // No repeated tokens means that there are no alternatives.
  @Test
  public void testNoRepetition() {
    SimpleWitness a = new SimpleWitness("a", "a b c d");
    SimpleWitness b = new SimpleWitness("b", "a b e d");

    collationAlgorithm = new DekkerDecisionTreeAlgorithm();
    collate(a, b);
    
    DekkerDecisionTreeAlgorithm dtalgo = (DekkerDecisionTreeAlgorithm) collationAlgorithm;

    // expand the tree twice (1 candidate -> 2 candidates -> 4 candidates)
    dtalgo.expandPossibleAlignments();
    dtalgo.expandPossibleAlignments();
    
    // select first vector graph, select second vector graph
    DecisionTreeNode c1 = dtalgo.possibleAlignments.get(0);
    assertEquals(2, c1.getNumberOfSelectedVectors());
    assertEquals(3, c1.getNumberOfAlignedTokens());
    assertEquals(1, c1.getNumberOfGapTokens());
    // select first vector graph, skip second vector graph
    DecisionTreeNode c2 = dtalgo.possibleAlignments.get(1);
    assertEquals(1, c2.getNumberOfSelectedVectors());
    assertEquals(2, c2.getNumberOfAlignedTokens());
    assertEquals(2, c2.getNumberOfGapTokens());
    // skip first vector graph, select second vector graph
    DecisionTreeNode c3 = dtalgo.possibleAlignments.get(2);
    assertEquals(1, c3.getNumberOfSelectedVectors());
    assertEquals(1, c3.getNumberOfAlignedTokens());
    assertEquals(3, c3.getNumberOfGapTokens());
    // skip first vector graph, skip second vector graph
    DecisionTreeNode c4 = dtalgo.possibleAlignments.get(3);
    assertEquals(0, c4.getNumberOfSelectedVectors());
    assertEquals(0, c4.getNumberOfAlignedTokens());
    assertEquals(4, c4.getNumberOfGapTokens());
  }

 // Token a is repeated
 // Decision tree should contain alternatives
 @Test
 public void testSingleRepeatedPhrase() {
   SimpleWitness a = new SimpleWitness("a", "a x b a");
   SimpleWitness b = new SimpleWitness("b", "a y b");

   collationAlgorithm = new DekkerDecisionTreeAlgorithm();
   collate(a, b);
   
   DekkerDecisionTreeAlgorithm dtalgo = (DekkerDecisionTreeAlgorithm) collationAlgorithm;
   
   // expand the tree twice (1 candidate -> ? candidates -> 6 candidates)
   dtalgo.expandPossibleAlignments();
   dtalgo.expandPossibleAlignments();
//   dtalgo.listPossibleAlignments();

   List<DecisionTreeNode> candidates = dtalgo.possibleAlignments;
   
   // best candidate
   DecisionTreeNode c2 = candidates.get(0);
   assertEquals(2, c2.getNumberOfSelectedVectors());
   assertEquals(2, c2.getNumberOfAlignedTokens());
   assertEquals(2, c2.getNumberOfGapTokens());
   //TODO: add more asserts!
   //fail();
 }
}
