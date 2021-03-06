/*
 * Copyright (c) 2015 The Interedition Development Group.
 *
 * This file is part of CollateX.
 *
 * CollateX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CollateX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CollateX.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.interedition.collatex.dekker;

import eu.interedition.collatex.AbstractTest;
import eu.interedition.collatex.CollationAlgorithmFactory;
import eu.interedition.collatex.Token;
import eu.interedition.collatex.VariantGraph;
import eu.interedition.collatex.Witness;
import eu.interedition.collatex.matching.EqualityTokenComparator;
import eu.interedition.collatex.simple.SimpleVariantGraphSerializer;
import eu.interedition.collatex.simple.SimpleWitness;
import org.junit.Test;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;

/**
 * @author Ronald Haentjens Dekker
 *         <p>
 *         This test class tests the PhraseMatchDetector and
 *         the TranspositionDetector
 */
public class AlignmentTest extends AbstractTest {

    @Test
    public void doubleTransposition1() {
        final SimpleWitness[] w = createWitnesses("the cat is black", "black is the cat");
        final List<SortedMap<Witness, Set<Token>>> t = table(collate(w));
        assertEquals("|the|cat|is|black| |", toString(t, w[0]));
        assertEquals("|black| |is|the|cat|", toString(t, w[1]));
    }

    @Test
    public void doubleTransposition2() {
        final SimpleWitness[] w = createWitnesses("a b", "b a");
        final List<SortedMap<Witness, Set<Token>>> t = table(collate(w));
        assertEquals("| |a|b|", toString(t, w[0]));
        assertEquals("|b|a| |", toString(t, w[1]));
    }

    @Test
    public void doubleTransposition3() {
        final SimpleWitness[] w = createWitnesses("a b c", "b a c");
        final List<SortedMap<Witness, Set<Token>>> t = table(collate(w));
        assertEquals("| |a|b|c|", toString(t, w[0]));
        assertEquals("|b|a| |c|", toString(t, w[1]));
    }

    @Test
    public void additionInCombinationWithTransposition() {
        final SimpleWitness[] w = createWitnesses(//
            "the cat is very happy",//
            "very happy is the cat",//
            "very delitied and happy is the cat");
        final List<SortedMap<Witness, Set<Token>>> t = table(collate(w));
        assertEquals("|the|cat| | |is|very|happy|", toString(t, w[0]));
        assertEquals("|very| | |happy|is|the|cat|", toString(t, w[1]));
        assertEquals("|very|delitied|and|happy|is|the|cat|", toString(t, w[2]));
    }

    @Test
    public void simpleTransposition() {
        final SimpleWitness[] w = createWitnesses(//
            "A black cat in a white basket",//
            "A white cat in a black basket");
        final List<SortedMap<Witness, Set<Token>>> t = table(collate(w));
        assertEquals("|a|black|cat|in|a|white|basket|", toString(t, w[0]));
        assertEquals("|a|white|cat|in|a|black|basket|", toString(t, w[1]));
    }

    @Test
    public void transposeInOnePair() {
        final SimpleWitness[] w = createWitnesses("y", "x y z", "z y");
        final List<SortedMap<Witness, Set<Token>>> t = table(collate(w));
        assertEquals("| |y| |", toString(t, w[0]));
        assertEquals("|x|y|z|", toString(t, w[1]));
        assertEquals("|z|y| |", toString(t, w[2]));
    }

    @Test
    public void transposeInTwoPairs() {
        final SimpleWitness[] w = createWitnesses("y x", "x y z", "z y");
        final List<SortedMap<Witness, Set<Token>>> t = table(collate(w));
        assertEquals("| |y|x|", toString(t, w[0]));
        assertEquals("|x|y|z|", toString(t, w[1]));
        assertEquals("|z|y| |", toString(t, w[2]));
    }

    @Test
    public void testOrderIndependence() {
        final SimpleWitness[] w = createWitnesses("Hello cruel world", "Hello nice world", "Hello nice cruel world");
        collate(w[0], w[1], w[2]);
        assertPhraseMatches("Hello", "nice", "cruel", "world");
        List<List<Match>> transpositions = ((DekkerAlgorithm) collationAlgorithm).getTranspositions();
        assertEquals(0, transpositions.size());
    }

    @Test
    public void testPhraseMatchingShouldNotIgnoreDeletions() {
        final SimpleWitness[] w = createWitnesses("Hello cruel world", "Hello world");
        collate(w);
        assertPhraseMatches("Hello", "world");
        List<List<Match>> transpositions = ((DekkerAlgorithm) collationAlgorithm).getTranspositions();
        assertEquals(0, transpositions.size());
    }

    @Test
    public void testPhraseMatchingShouldNotIgnoreAdditions() {
        final SimpleWitness[] w = createWitnesses("Hello world", "Hello cruel world");
        collate(w);
        assertPhraseMatches("Hello", "world");
        List<List<Match>> transpositions = ((DekkerAlgorithm) collationAlgorithm).getTranspositions();
        assertEquals(0, transpositions.size());
    }

    @Test
    public void testOrderIndependenceTroy() throws XMLStreamException {
        final List<SimpleWitness> witnesses = new ArrayList<>();
        witnesses.add(new SimpleWitness("w1", "X A Z "));
        witnesses.add(new SimpleWitness("w2", "Y B Z "));
        witnesses.add(new SimpleWitness("w3", "Y A X "));

        setCollationAlgorithm(CollationAlgorithmFactory.dekker(new EqualityTokenComparator()));
        VariantGraph graph = new VariantGraph();
        collationAlgorithm.collate(graph, witnesses.toArray(new SimpleWitness[0]));

        StringWriter writer = new StringWriter();
        XMLStreamWriter swriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        swriter.writeStartDocument();
        new SimpleVariantGraphSerializer(graph).toGraphML(swriter);
        swriter.writeEndDocument();
    }
}
