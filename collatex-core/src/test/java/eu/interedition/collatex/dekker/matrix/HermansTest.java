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

package eu.interedition.collatex.dekker.matrix;

import eu.interedition.collatex.AbstractTest;
import eu.interedition.collatex.CollationAlgorithmFactory;
import eu.interedition.collatex.Token;
import eu.interedition.collatex.VariantGraph;
import eu.interedition.collatex.Witness;
import eu.interedition.collatex.matching.EqualityTokenComparator;
import eu.interedition.collatex.matching.StrictEqualityTokenComparator;
import eu.interedition.collatex.simple.SimpleToken;
import eu.interedition.collatex.simple.SimpleVariantGraphSerializer;
import eu.interedition.collatex.simple.SimpleWitness;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HermansTest extends AbstractTest {

    @Before
    public void setUp() {
        setCollationAlgorithm(CollationAlgorithmFactory.dekkerMatchMatrix(new StrictEqualityTokenComparator(), 1));
    }

    @Test
    public void testHermansText1() {
        String textD1 = "Op den Atlantischen Oceaan voer een groote stoomer, de lucht was helder blauw, het water rimpelend satijn.";
        String textD9 = "Over de Atlantische Oceaan voer een grote stomer. De lucht was helder blauw, het water rimpelend satijn.<p/>";
        SimpleWitness[] sw = createWitnesses(textD1, textD9);
        VariantGraph vg = collate(sw[0]);
        MatchTable buildMatrix = MatchTableImpl.create(vg, sw[1], new EqualityTokenComparator());
        LOG.fine(new MatchTableSerializer(buildMatrix).toHtml());
    }

    @Test
    public void testHermansText2() {
        String textD1 = "Op den Atlantischen Oceaan voer een groote stoomer. Onder de velen aan boojrd bevond zich een bruine, korte dikke man. <i> JSg </i> werd nooit zonder sigaar gezien. Zijn pantalon had lijnrechte vouwen in de pijpen, maar zat toch altijd vol rimpels. <b> De </b> pantalon werd naar boven toe breed, ontzaggelijk breed; hij omsloot den buik van den kleinen man als een soort balcon.";
        String textD9 = "Op de Atlantische Oceaan voer een ontzaggelijk zeekasteel. Onder de vele passagiers aan boord, bevond zich een bruine, korte dikke man. Hij werd nooit zonder sigaar gezien. Zijn pantalon had lijnrechte vouwen in de pijpen, maar zat toch altijd vol rimpels. De pantalon werd naar boven toe breed, ongelofelijk breed: hij omsloot de buik van de kleine man als een soort balkon.";
        SimpleWitness[] sw = createWitnesses(textD1, textD9);
        VariantGraph vg = collate(sw[0]);
        MatchTable matchTable = MatchTableImpl.create(vg, sw[1], new EqualityTokenComparator());
        // System.out.println(buildMatrix.toHtml());
        IslandConflictResolver archipelago = new IslandConflictResolver(matchTable);
//    LOG.fine("archipelago: " + archipelago);
//    LOG.fine("archipelago.size(): " + archipelago.size());
//    assertEquals(42, archipelago.size());
//    assertEquals(98, archipelago.numOfConflicts());
        // assertTrue(false);
        // archipelago.createNonConflictingVersions();
        // assertEquals(603,archipelago.numOfNonConflConstell());
        // assertEquals(500,archipelago.getVersion(0).value());
        // assertEquals(497,archipelago.getVersion(4).value());

        MatchTableSelection firstVersion = archipelago.createNonConflictingVersion();
        for (Island isl : firstVersion.getIslands()) {
            LOG.fine(" " + isl.size());
        }
        // for(int i=0; i<10; i++) {
        // try {
        // String file_name = "result_"+i+".html";
        // File logFile = new File(File.separator +
        // "C:\\Documents and Settings\\meindert\\Mijn Documenten\\Project Hermans productielijn\\Materiaal input collateX\\output_collatex_exp\\"+file_name);
        // PrintWriter logging = new PrintWriter(new FileOutputStream(logFile));
        // logging.println(buildMatrix.toHtml(archipelago.getVersion(i)));
        // logging.close();
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // }
        // }

    }

    @Ignore("Archipelago size changed?")
    @Test
    public void testHermansText3() {
        String textMZ_DJ233 = "Werumeus Buning maakt artikelen van vijf pagina&APO+s over de geologie van de diepzee, die hij uit Engelse boeken overschrijft, wat hij pas in de laatste regel vermeldt, omdat hij zo goed kan koken.<p/>\n" + "J. W. Hofstra kan niet lezen en nauwelijks stotteren, laat staan schrijven. Hij oefent het ambt van litterair criticus uit omdat hij uiterlijk veel weg heeft van een Duitse filmacteur (Adolf Wohlbrock).<p/>\n" + "Zo nu en dan koopt Elsevier een artikel van een echte professor wiens naam en titels zu vet worden afgedrukt, dat zij allicht de andere copie ook iets professoraals geven, in het oog van de speksnijders.<p/>\n" + "Edouard Bouquin is het olijke culturele geweten. Bouquin betekent: 1) oud boek van geringe waarde, 2) oude bok, 3) mannetjeskonijn. Ik kan het ook niet helpen, het staat in Larousse.<p/>\n" + "De politiek van dit blad wordt geschreven door een der leeuwen uit het Nederlandse wapen (ik geloof de rechtse) op een krakerige gerechtszaaltoon in zeer korte zinnetjes, omdat hij tot zijn spijt de syntaxis onvoldoende beheerst.<p/>\n";
        String textD4F = "Werumeus  Buning maakt artikelen van vijf pagina&APO+s  over de  geologie van de  diepzee, die  hij uit Engelse  boeken overschrijft,   wat hij  pas in de laatste  regel  vermeldt,   omdat hij   zo  goed kan koken.<p/>\n" + "J. W.Hofstra kan niet lezen en nauwelijks stotteren,   laat staan schrijven.   Hij  oefent het ambt van literair kritikus uit omdat hij uiterlijk veel weg heeft van een Duitse filmacteur (Adolf Wohlbrock).<p/>\n" + "Edouard  Bouquin is  het olijke  culturele  geweten.   Bouquin betekent:   1)  oud boek  van geringe  waarde,   2)  oude bok,   3)  mannetjeskonijn.   Ik kan het ook niet helpen,   het staat in Larousse.<p/>\n" + "Nu en dan koopt Elsevier een artikel van een echte professor, wiens naam en titels zu vet worden afgedrukt, dat zij allicht de andere copie ook iets professoraals geven, in het oog van de speksnijders.<p/>\n" + "\n" + "De politiek van dit blad  wordt geschreven door een der leeuwen uit het nederlandse wapen (ik geloof de   rechtse)  op een krakerige  gerechtszaaltoon in zeer korte  zinnetjes, omdat hij  tot zijn  spijt  de  syntaxis  onvoldoende  beheerst. <p/>";
        SimpleWitness[] sw = createWitnesses(textMZ_DJ233, textD4F);
        VariantGraph vg = collate(sw[0]);
        MatchTable matchTable = MatchTableImpl.create(vg, sw[1], new EqualityTokenComparator());
        // try {
        // FileWriter fw = new
        // FileWriter("C:\\Documents and Settings\\meindert\\Mijn Documenten\\Project Hermans productielijn\\Materiaal input collateX\\Hulp1.html");
        // fw.write(buildMatrix.toHtml());
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // System.out.println(buildMatrix.toHtml());
        IslandConflictResolver archipelago = new IslandConflictResolver(matchTable);
//    LOG.fine("archipelago: " + archipelago);
//    LOG.fine("archipelago.size(): " + archipelago.size());
        MatchTableSelection firstVersion = archipelago.createNonConflictingVersion();
        for (Island isl : firstVersion.getIslands()) {
            LOG.fine(" " + isl.size());
        }
        //    assertEquals(4877, firstVersion.value());
        // assertTrue(false);

        // archipelago.createNonConflictingVersions();
        // assertEquals(603,archipelago.numOfNonConflConstell());
        // assertEquals(500,archipelago.getVersion(0).value());
        // assertEquals(497,archipelago.getVersion(4).value());
        // for(int i=0; i<10; i++) {
        // try {
        // String file_name = "result_2_"+i+".html";
        // File logFile = new File(File.separator +
        // "C:\\Documents and Settings\\meindert\\Mijn Documenten\\Project Hermans productielijn\\Materiaal input collateX\\output_collatex_exp\\"+file_name);
        // PrintWriter logging = new PrintWriter(new FileOutputStream(logFile));
        // logging.println(buildMatrix.toHtml(archipelago.getVersion(i)));
        // logging.close();
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // }
        // }
    }

    @Test
    public void testHermansTextWithOutliers() throws XMLStreamException {
        String textD1 = "Op den Atlantischen Oceaan voer een groote stoomer, de lucht was helder blauw, het water rimpelend satijn. Op den Atlantischen Oceaan voer een groote stoomer. Onder de velen aan boojrd bevond zich een bruine, korte dikke man. <i> JSg </i> werd nooit zonder sigaar gezien. Zijn pantalon had lijnrechte vouwen in de pijpen, maar zat toch altijd vol rimpels. <b> De </b> pantalon werd naar boven toe breed, ontzaggelijk breed; hij omsloot den buik van den kleinen man als een soort balcon.";
        String textD9 = "Over de Atlantische Oceaan voer een grote stomer. De lucht was helder blauw, het water rimpelend satijn.<p/> Op de Atlantische Oceaan voer een ontzaggelijk zeekasteel. Onder de vele passagiers aan boord, bevond zich een bruine, korte dikke man. Hij werd nooit zonder sigaar gezien. Zijn pantalon had lijnrechte vouwen in de pijpen, maar zat toch altijd vol rimpels. De pantalon werd naar boven toe breed, ongelofelijk breed: hij omsloot de buik van de kleine man als een soort balkon.";
        SimpleWitness[] witnesses = createWitnesses(textD1, textD9);

        testWitnessCollation(witnesses);
    }

    @Test
    public void testHermansText2b() throws XMLStreamException {
        String textD1 = "Op den Atlantischen Oceaan voer een groote stoomer, de lucht was helder blauw, het water rimpelend satijn. Op den Atlantischen Oceaan voer een groote stoomer. Onder de velen aan boojrd bevond zich een bruine, korte dikke man. <i> JSg </i> werd nooit zonder sigaar gezien. Zijn pantalon had lijnrechte vouwen in de pijpen, maar zat toch altijd vol rimpels. <b> De </b> pantalon werd naar boven toe breed, ontzaggelijk breed; hij omsloot den buik van den kleinen man als een soort balcon.";
        String textD9 = "Over de Atlantische Oceaan voer een grote stomer. De lucht was helder blauw, het water rimpelend satijn.<p/> Op de Atlantische Oceaan voer een ontzaggelijk zeekasteel. Onder de vele passagiers aan boord, bevond zich een bruine, korte dikke man. Hij werd nooit zonder sigaar gezien. Zijn pantalon had lijnrechte vouwen in de pijpen, maar zat toch altijd vol rimpels. De pantalon werd naar boven toe breed, ongelofelijk breed: hij omsloot de buik van de kleine man als een soort balkon.";
        String textDMD1 = "Over de Atlantische Oceaan voer een grote stomer. De lucht was helder blauw, het water rimpelend satijn.<p/>\nOp sommige dekken van de stomer lagen mensen in de zon, op andere dekken werd getennist, op nog andere liepen de passagiers heen en weer en praatten. Wie over de reling hing en recht naar beneden keek, kon vaststellen dat het schip vorderde; of draaide alleen de aarde er onderdoor?<p/>\nOp de Atlantische Oceaan voer een ontzaggelijk zeekasteel. Onder de vele passagiers aan boord, bevond zich een bruine, korte dikke man. Hij werd nooit zonder sigaar gezien. Zijn pantalon had lijnrechte vouwen in de pijpen, maar zat toch altijd vol rimpels. De pantalon werd naar boven toe breed, ongelofelijk breed: hij omsloot de buik van de kleine man als een soort balkon.<p/>";
        SimpleWitness[] witnesses = createWitnesses(textD1, textD9, textDMD1);

        testWitnessCollation(witnesses);
    }

    @Test
    public void testHermansText2c() throws XMLStreamException {
        String textD1 = "Op den Atlantischen Oceaan voer een groote stoomer.";
        String textD9 = "Over de Atlantische Oceaan voer een grote stomer.";
        String textDMD1 = "Over de Atlantische Oceaan voer een vreselijk grote stomer.";
        SimpleWitness[] witnesses = createWitnesses(textD1, textD9, textDMD1);

        testWitnessCollation(witnesses);
    }


    private void testWitnessCollation(SimpleWitness[] sw) throws XMLStreamException, FactoryConfigurationError {
        VariantGraph vg = collate(sw);
        //    List<VariantGraphVertex> v = Lists.newArrayList(vg.vertices());
        String teiMM = generateTEI(vg);
        assertNotNull(teiMM);
        LOG.fine(teiMM);

        //    setCollationAlgorithm(CollationAlgorithmFactory.dekker(new EqualityTokenComparator()));
        //    vg = collate(sw);
        //    String teiD = generateTEI(vg);
        //    LOG.debug(teiD);
        //    assertNotNull(teiD);
        //    assertFalse(teiD.equals(teiMM));
    }

    @Ignore("The b-transposition is one token short")
    @Test
    public void test4JoinedTranspositions2witnesses() throws XMLStreamException {
        String a = "a1 a2 a3 b1 b2 b3 c1 c2 c3 d1 d2 d3";
        String b = "d1 d2 d3 a1 a2 a3 c1 c2 c3 b1 b2 b3";
        SimpleWitness[] sw = createWitnesses(a, b);
        //    testWitnessCollation(sw);
        VariantGraph vg = collate(sw);
        Set<Set<VariantGraph.Vertex>> transpositions0 = vg.transpositions();
        for (Set<VariantGraph.Vertex> t : transpositions0) {
            LOG.log(Level.FINE, "transposition {0}", t.toString());
        }

        Iterable<VariantGraph.Vertex> vertices = vg.vertices();
        for (VariantGraph.Vertex v : vertices) {
            LOG.log(Level.FINE, "vertex:{0}, transpositions:{1}", new Object[]{v, v.transpositions().toString()});
        }
        vg = VariantGraph.JOIN.apply(vg);
        LOG.fine(toString(table(vg)));
        Set<Set<VariantGraph.Vertex>> transpositions = vg.transpositions();
        LOG.log(Level.FINE, "{0} transpositions", transpositions.size());
        for (Set<VariantGraph.Vertex> t : transpositions) {
            LOG.log(Level.FINE, "transposition {0}", t.toString());
            // all joined vertices should be size 3
            for (VariantGraph.Vertex vertex : t) {
                assertEquals(t.toString(), 3, vertex.tokens().size());
            }
        }
        assertEquals(3, transpositions.size());
    }

    @Ignore("Not all transpositions detected anymore?")
    @Test
    public void test4JoinedTranspositions3witnesses() throws XMLStreamException {
        String a = "a1 a2 a3 b1 b2 b3 c1 c2 c3 d1 d2 d3";
        String b = "d1 d2 d3 a1 a2 a3 c1 c2 c3 b1 b2 b3";
        String c = "c1 c2 c3 b1 b2 b3 d1 d2 d3 a1 a2 a3";
        SimpleWitness[] sw = createWitnesses(a, b, c);
        //    testWitnessCollation(sw);
        VariantGraph vg = collate(sw);

        Iterable<VariantGraph.Vertex> vertices = vg.vertices();
        for (VariantGraph.Vertex v : vertices) {
            LOG.log(Level.FINE, "vertex:{0}, transpositions:{1}", new Object[]{v, v.transpositions()});
        }

        final StringWriter writer = new StringWriter();
        new SimpleVariantGraphSerializer(vg).toDot(writer);
        LOG.fine(writer.toString());

        vg = VariantGraph.JOIN.apply(vg);
        Set<Set<VariantGraph.Vertex>> transpositions = vg.transpositions();
        LOG.log(Level.FINE, "{0} transpositions", transpositions.size());
        for (Set<VariantGraph.Vertex> t : transpositions) {
            String showTransposition = t.toString();
            LOG.log(Level.FINE, "transposition {0}", showTransposition);
            boolean transpositionOfA = showTransposition.contains("a");
            boolean transpositionOfB = showTransposition.contains("b");
            boolean transpositionOfC = showTransposition.contains("c");
            boolean transpositionOfD = showTransposition.contains("d");
            // there should be only a, b, c or d in the transpositions
            assertTrue(transpositionOfA ^ transpositionOfB ^ transpositionOfC ^ transpositionOfD);
            //      assertEquals(showTransposition(t), 3, t.from().tokens().size());
            //      assertEquals(showTransposition(t), 3, t.to().tokens().size());
        }
        assertEquals(7, transpositions.size());
    }

    @Test
    public void testHermansText3aJoinedTranspositions() throws XMLStreamException {
        String textMZ_DJ233 = "Werumeus Buning maakt artikelen van vijf pagina&APO+s over de geologie van de diepzee, die hij uit Engelse boeken overschrijft, wat hij pas in de laatste regel vermeldt, omdat hij zo goed kan koken.<p/>\nJ. W. Hofstra kan niet lezen en nauwelijks stotteren, laat staan schrijven. Hij oefent het ambt van litterair criticus uit omdat hij uiterlijk veel weg heeft van een Duitse filmacteur (Adolf Wohlbrock).<p/>\nZo nu en dan koopt Elsevier een artikel van een echte professor wiens naam en titels zu vet worden afgedrukt, dat zij allicht de andere copie ook iets professoraals geven, in het oog van de speksnijders.<p/>\nEdouard Bouquin is het olijke culturele geweten. Bouquin betekent: 1) oud boek van geringe waarde, 2) oude bok, 3) mannetjeskonijn. Ik kan het ook niet helpen, het staat in Larousse.<p/>\nDe politiek van dit blad wordt geschreven door een der leeuwen uit het Nederlandse wapen (ik geloof de rechtse) op een krakerige gerechtszaaltoon in zeer korte zinnetjes, omdat hij tot zijn spijt de syntaxis onvoldoende beheerst.<p/>\nAldus de artikelen van Werumeus Buning";
        String textD4F = "Werumeus  Buning maakt machtigmooie artikelen van vijf pagina&APO+s  over de  geologie van de  diepzee, die  hij uit Engelse  boeken overschrijft,   wat hij  pas in de laatste  regel  vermeldt,   omdat hij   zo  goed kan koken.<p/>\nJ. W.Hofstra kan niet lezen en nauwelijks stotteren,   laat staan schrijven.   Hij  oefent het ambt van literair kritikus uit omdat hij uiterlijk veel weg heeft van een Duitse filmacteur (Adolf Wohlbrock).<p/>\nEdouard  Bouquin is  het olijke  culturele  geweten.   Bouquin betekent:   1)  oud boek  van geringe  waarde,   2)  oude bok,   3)  mannetjeskonijn.   Ik kan het ook niet helpen,   het staat in Larousse.<p/>\nNu en dan koopt Elsevier een artikel van een echte professor, wiens naam en titels zu vet worden afgedrukt, dat zij allicht de andere copie ook iets professoraals geven, in het oog van de speksnijders.<p/>\n\nDe politiek van dit blad  wordt geschreven door een der leeuwen uit het nederlandse wapen (ik geloof de   rechtse)  op een krakerige  gerechtszaaltoon in zeer korte  zinnetjes, omdat hij  tot zijn  spijt  de  syntaxis  onvoldoende  beheerst. <p/>Volgens de stukjes van Werumeus Buning";
        SimpleWitness[] sw = createWitnesses(textMZ_DJ233, textD4F);
        testWitnessCollation(sw);
    }

    //  @Test
    public void testHermansText3aJoinedTranspositions2() throws XMLStreamException {
        String textD1 = "Over hem waakten de @Dochters Zions# naar Micha 4:13 of ook genaamd de @Zonen van Dan (Gen. 49:17)";
        String textD9 = "Over hem waakte een garde, genaamd de @Dochter Zions# naar Micha 4 13, of ook de ,,/onen van Dan#<b> Gen</b> 49 17";
        String textDMD1 = "Over hem waakte een garde, genaamd de ,,Dochter Zions# naar Micha 4 : 13, of ook de @zonen van Dan# Gen. 49 : 17.";
        String textDMD5 = "Over hem waakte een garde, genaamd de @Dochter Zions# naar Micha 4 . 13, of ook de @zonen van Dan# Gen 49 17";

        SimpleWitness[] sw = createWitnesses(textD1, textD9, textDMD1, textDMD5);
        testWitnessCollation(sw);

        VariantGraph vg = VariantGraph.JOIN.apply(collate(sw));
        Set<Set<VariantGraph.Vertex>> transpositions = vg.transpositions();
        assertEquals(5, transpositions.size());
        Set<VariantGraph.Vertex> transposition = transpositions.iterator().next();
        //    assertEquals("genaamd de", transposition.from().toString());
    }

    @Test
    public void testTEI() throws XMLStreamException, FactoryConfigurationError {
        String text1 = "voor Zo nu en dan zin2 na voor";
        String text2 = "voor zin2 Nu en dan voor";
        SimpleWitness[] sw = createWitnesses(text1, text2);
        testWitnessCollation(sw);
    }

    @Test
    public void testHermansAllesIsBetrekkelijk() throws XMLStreamException {
        String textD1 = "Zij had gelijk; natuurlijk is alles betrekkelijk en het hangt er van af hoe men het gewend is.";
        String textD9 = "Zij had gelijk. Natuurlijk, alles is&KOP+betrekkelijk en het hangt er alleen van af\n |P 46|\nhoe men het gewend is.";
        String textDmd1 = "Zij had gelijk. Natuurlijk, alles is betrekkelijk en het hangt er alleen van af\n&WR+\n46<p/>\nhoe men het gewend is.";
        String textDmd9 = "Zij had gelijk. Natuurlijk, alles is&KOP+betrekkelijk en het hangt er alleen van af\n |P 46|\nhoe men het gewend is.";
        SimpleWitness[] sw = createWitnesses(textD1, textD9, textDmd1, textDmd9);
        testWitnessCollation(sw);
    }

    @Test
    public void testHermansAllesIsBetrekkelijk1() throws XMLStreamException {
        String textD1 = "natuurlijk is alles betrekkelijk";
        String textD9 = "Natuurlijk, alles mag relatief zijn";
        String textDmd1 = "Natuurlijk, alles is betrekkelijk";
        SimpleWitness[] sw = createWitnesses(textD1, textD9, textDmd1);
        testWitnessCollation(sw);
    }

    /* TODO: Find solution for rendering transposition correctly
     * with more than two witnesses.
     */
    @Ignore
    @Test
    public void testNoLoops() throws XMLStreamException {
        String w1 = "a b c d";
        String w2 = "e c f g";
        String w3 = "e c b d";
        SimpleWitness[] sw = createWitnesses(w1, w2, w3);
        VariantGraph vg = collate(sw);
        Set<Set<VariantGraph.Vertex>> transpositions = vg.transpositions();
        assertEquals(1, transpositions.size());
        Set<VariantGraph.Vertex> t = transpositions.iterator().next();
        for (VariantGraph.Vertex vertex : t) {
            for (SimpleToken token : vertex.tokens().stream().map(tk -> (SimpleToken) tk).toArray(SimpleToken[]::new)) {
                assertEquals(token.toString(), token.getNormalized(), "c");
            }
        }
        final Set<Witness> witnessesInTransposition = new HashSet<>();
        for (VariantGraph.Vertex vertex : t) {
            for (Token token : vertex.tokens()) {
                witnessesInTransposition.add(token.getWitness());
            }
        }
        assertEquals(new HashSet<>(Arrays.asList(sw)), witnessesInTransposition);
    }

    //  @Test
    //  public void testHermansText4() throws XMLStreamException {
    //    String textMZ_DJ233 = "Werumeus Buning maakt artikelen van vijf pagina&APO+s over de geologie van de diepzee, die hij uit Engelse boeken overschrijft, wat hij pas in de laatste regel vermeldt, omdat hij zo goed kan koken.<p/>\nJ. W. Hofstra kan niet lezen en nauwelijks stotteren, laat staan schrijven. Hij oefent het ambt van litterair criticus uit omdat hij uiterlijk veel weg heeft van een Duitse filmacteur (Adolf Wohlbrock).<p/>\nZo nu en dan koopt Elsevier een artikel van een echte professor wiens naam en titels zu vet worden afgedrukt, dat zij allicht de andere copie ook iets professoraals geven, in het oog van de speksnijders.<p/>\nEdouard Bouquin is het olijke culturele geweten. Bouquin betekent: 1) oud boek van geringe waarde, 2) oude bok, 3) mannetjeskonijn. Ik kan het ook niet helpen, het staat in Larousse.<p/>\nDe politiek van dit blad wordt geschreven door een der leeuwen uit het Nederlandse wapen (ik geloof de rechtse) op een krakerige gerechtszaaltoon in zeer korte zinnetjes, omdat hij tot zijn spijt de syntaxis onvoldoende beheerst.<p/>\nAldus de artikelen van Werumeus Buning";
    //    String textD4F = "Werumeus  Buning maakt machtigmooie artikelen van vijf pagina&APO+s  over de  geologie van de  diepzee, die  hij uit Engelse  boeken overschrijft,   wat hij  pas in de laatste  regel  vermeldt,   omdat hij   zo  goed kan koken.<p/>\nJ. W.Hofstra kan niet lezen en nauwelijks stotteren,   laat staan schrijven.   Hij  oefent het ambt van literair kritikus uit omdat hij uiterlijk veel weg heeft van een Duitse filmacteur (Adolf Wohlbrock).<p/>\nEdouard  Bouquin is  het olijke  culturele  geweten.   Bouquin betekent:   1)  oud boek  van geringe  waarde,   2)  oude bok,   3)  mannetjeskonijn.   Ik kan het ook niet helpen,   het staat in Larousse.<p/>\nNu en dan koopt Elsevier een artikel van een echte professor, wiens naam en titels zu vet worden afgedrukt, dat zij allicht de andere copie ook iets professoraals geven, in het oog van de speksnijders.<p/>\n\nDe politiek van dit blad  wordt geschreven door een der leeuwen uit het nederlandse wapen (ik geloof de   rechtse)  op een krakerige  gerechtszaaltoon in zeer korte  zinnetjes, omdat hij  tot zijn  spijt  de  syntaxis  onvoldoende  beheerst. <p/>Volgens de stukjes van Werumeus Buning";
    //    SimpleWitness[] sw = createWitnesses(textMZ_DJ233, textD4F);
    //    testWitnessCollation(sw);
    //  }

    private String generateTEI(VariantGraph vg) throws XMLStreamException, FactoryConfigurationError {
        SimpleVariantGraphSerializer s = new SimpleVariantGraphSerializer(VariantGraph.JOIN.apply(vg));
        StringWriter writer = new StringWriter();
        s.toDot(writer);
        LOG.fine(writer.toString());
        XMLStreamWriter xml = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        xml.writeStartDocument();
        s.toTEI(xml);
        xml.writeEndDocument();
        return writer.toString();
    }

}
