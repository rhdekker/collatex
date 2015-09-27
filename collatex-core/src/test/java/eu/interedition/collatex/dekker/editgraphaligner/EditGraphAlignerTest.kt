package eu.interedition.collatex.dekker.editgraphaligner

import eu.interedition.collatex.VariantGraph
import eu.interedition.collatex.simple.SimpleCollation
import eu.interedition.collatex.simple.SimpleWitness
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

/**
 * Created by ronalddekker on 24/09/15.
 */



/**
 * JUnit 4 Test Case
 */
public class JUnit4StringTest {

    @Before fun setUp() {
        // set up the test case
    }

    @After fun tearDown() {
        // tear down the test case
    }

    @Test fun testCapitalize() {
        // assertEquals comes from kotlin.test.*
        assertEquals("Hello world!", "hello world!".capitalize())
    }


    // global score (gaps)
    fun assertRow(expected: ArrayList<Int>, cell_data: Array<EditGraphTable.TableCell?>) {
        val actual = ArrayList<Int>()
        for (cell in cell_data) {
            actual.add(cell!!.g)
        }
        assertEquals(expected, actual)
    }

    @Test fun testOmission() {
        val witnesses = ArrayList<SimpleWitness>()
        witnesses.add(SimpleWitness("A", "a b c"))
        witnesses.add(SimpleWitness("B", "b c"))
        val algorithm = EditGraphAligner()
        val collation = SimpleCollation(witnesses, algorithm, true)
        val graph = VariantGraph()
        collation.collate(graph)
        val table = algorithm.table
        assertRow(arrayListOf(0, -1, -2, -3), table!![0])
    }


//    # we need to introduce a gap here
//    def testOmission(self):
//    collation = Collation()
//    collation.add_plain_witness("A", "a b c")
//    collation.add_plain_witness("B", "b c")
//    scorer = Scorer(collation)
//    aligner = EditGraphAligner(collation, scorer)
//    graph = VariantGraph()
//    aligner.collate(graph, collation)
//    table = aligner.table
//    #         self.debug_table(aligner, table)
//    self.assertEqual(0, table[0][0].g)
//    self.assertEqual(-1, table[0][1].g)
//    self.assertEqual(-2, table[0][2].g)
//    self.assertEqual(-3, table[0][3].g)
//    self.assertEqual(-1, table[1][0].g)
//    self.assertEqual(-2, table[1][1].g)
//    self.assertEqual(-1, table[1][2].g)
//    self.assertEqual(-2, table[1][3].g)
//    self.assertEqual(-2, table[2][0].g)
//    self.assertEqual(-3, table[2][1].g)
//    self.assertEqual(-2, table[2][2].g)
//    self.assertEqual(-1, table[2][3].g)
}