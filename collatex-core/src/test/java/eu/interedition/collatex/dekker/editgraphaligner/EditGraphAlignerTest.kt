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
        assertRow(arrayListOf(-1, -2, -1, -2), table!![1])
        assertRow(arrayListOf(-2, -3, -2, -1), table!![2])
    }
}