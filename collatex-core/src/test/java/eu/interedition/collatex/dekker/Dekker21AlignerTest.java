package eu.interedition.collatex.dekker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import eu.interedition.collatex.AbstractTest;
import eu.interedition.collatex.VariantGraph;
import eu.interedition.collatex.dekker.Dekker21Aligner.DecisionGraphNode;
import eu.interedition.collatex.simple.SimpleWitness;

public class Dekker21AlignerTest extends AbstractTest {

    private void assertLCP_Interval(int start, int length, int depth, LCP_Interval lcp_interval) {
        assertEquals(start, lcp_interval.start);
        assertEquals(length, lcp_interval.length);
        assertEquals(depth, lcp_interval.depth());
    }

    private void assertNode(int i, int j, Dekker21Aligner.EditOperationEnum editOperation, DecisionGraphNode decisionGraphNode) {
        assertEquals(i, decisionGraphNode.startPosWitness1);
        assertEquals(j, decisionGraphNode.startPosWitness2);
        assertEquals(editOperation, decisionGraphNode.editOperation);
    }

    @Test
    public void testCaseDanielStoekl() {
        // 1: a, b, c, d, e
        // 2: a, e, c, d
        // 3: a, d, b
        final SimpleWitness[] w = createWitnesses("a b c d e", "a e c d", "a d b");
        Dekker21Aligner aligner = new Dekker21Aligner(w);
        //Note: the suffix array can have multiple forms
        //outcome of sorting is not guaranteed
        //however the LCP array is fixed we can assert that
        assertEquals("[-1, 1, 1, 0, 1, 0, 2, 0, 1, 1, 0, 1]", Arrays.toString(aligner.LCP_array));
    }

    @Test
    public void testCaseDanielStoeklLCPIntervals() {
        // 1: a, b, c, d, e
        // 2: a, e, c, d
        // 3: a, d, b
        final SimpleWitness[] w = createWitnesses("a b c d e", "a e c d", "a d b");
        Dekker21Aligner aligner = new Dekker21Aligner(w);
        List<LCP_Interval> lcp_intervals = aligner.splitLCP_ArrayIntoIntervals();
        assertLCP_Interval(0, 1, 3, lcp_intervals.get(0)); // a
        assertLCP_Interval(3, 1, 2, lcp_intervals.get(1)); // b
        assertLCP_Interval(5, 2, 2, lcp_intervals.get(2)); // c d
        assertLCP_Interval(7, 1, 3, lcp_intervals.get(3)); // d
        assertLCP_Interval(10, 1, 2, lcp_intervals.get(4)); // e
        assertEquals(5, lcp_intervals.size());
    }

    @Test
    public void testCaseDecisionGraphNeighbours() {
        // 1: a, b, c, d, e
        // 2: a, e, c, d
        // 3: a, d, b
        final SimpleWitness[] w = createWitnesses("a b c d e", "a e c d", "a d b");
        Dekker21Aligner aligner = new Dekker21Aligner(w);
        VariantGraph against = new VariantGraph();
        aligner.collate(against, w);

        DecisionGraphNode root = aligner.new DecisionGraphNode();
        Dekker21Aligner.TwoDimensionalDecisionGraph gr = aligner.getDecisionGraph();

        Iterator<DecisionGraphNode> neighbours = gr.neighborNodes(root).iterator();
        assertNode(1,0, Dekker21Aligner.EditOperationEnum.SKIP_TOKEN_GRAPH, neighbours.next());
        assertNode(0,1, Dekker21Aligner.EditOperationEnum.SKIP_TOKEN_WITNESS, neighbours.next());
        assertNode(1,1, Dekker21Aligner.EditOperationEnum.MATCH_TOKENS_OR_REPLACE, neighbours.next());
        assertFalse(neighbours.hasNext());
    }

    @Test
    public void testCaseDecisionGraphHeuristicCostFunction() {
        // 1: a, b, c, d, e
        // 2: a, e, c, d
        // 3: a, d, b
        final SimpleWitness[] w = createWitnesses("a b c d e", "a e c d", "a d b");
        Dekker21Aligner aligner = new Dekker21Aligner(w);
        VariantGraph against = new VariantGraph();
        aligner.collate(against, w);
        DecisionGraphNode root = aligner.new DecisionGraphNode();
        Dekker21Aligner.TwoDimensionalDecisionGraph decisionGraph = aligner.getDecisionGraph();

        Iterator<DecisionGraphNode> neighbours = decisionGraph.neighborNodes(root).iterator();
        assertEquals(4, decisionGraph.heuristicCostEstimate(neighbours.next()).alignedTokens);
        assertEquals(3, decisionGraph.heuristicCostEstimate(neighbours.next()).alignedTokens);
        assertEquals(3, decisionGraph.heuristicCostEstimate(neighbours.next()).alignedTokens);
    }

    @Test
    public void testCaseDecisionGraphdistanceFunction() {
        // 1: a, b, c, d, e
        // 2: a, e, c, d
        // 3: a, d, b
        final SimpleWitness[] w = createWitnesses("a b c d e", "a e c d", "a d b");
        Dekker21Aligner aligner = new Dekker21Aligner(w);
        VariantGraph against = new VariantGraph();
        aligner.collate(against, w);
        DecisionGraphNode root = aligner.new DecisionGraphNode();
        Dekker21Aligner.TwoDimensionalDecisionGraph decisionGraph = aligner.getDecisionGraph();

        Iterator<DecisionGraphNode> neighbours = decisionGraph.neighborNodes(root).iterator();
        assertEquals(0, decisionGraph.distBetween(root, neighbours.next()).alignedTokens);
        assertEquals(0, decisionGraph.distBetween(root, neighbours.next()).alignedTokens);
        assertEquals(1, decisionGraph.distBetween(root, neighbours.next()).alignedTokens);
    }


}
