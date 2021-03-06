package eu.interedition.collatex.dekker.editgraphaligner

import eu.interedition.collatex.AbstractTest
import eu.interedition.collatex.Token
import eu.interedition.collatex.VariantGraph
import eu.interedition.collatex.simple.SimpleCollation
import eu.interedition.collatex.simple.SimpleToken
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
public class JUnit4StringTest : AbstractTest() {

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

    fun assertSuperbaseEquals(expected: String, superbase: List<Token>) {
        var actual = ""
        for (token in superbase) {
            if (actual > "") {
                actual += " "
            }
            actual += (token as SimpleToken).content.trim()
        }
        assertEquals(expected, actual)
    }

    @Test fun testOmission() {
        val witnesses = ArrayList<SimpleWitness>()
        val w0 = SimpleWitness("A", "a b c")
        val w1 = SimpleWitness("B", "b c")
        witnesses.add(w0)
        witnesses.add(w1)
        val algorithm = EditGraphAligner()
        val collation = SimpleCollation(witnesses, algorithm, false)
        val graph = VariantGraph()
        collation.collate(graph)
        // assert gaps in edit graph table
        val table = algorithm.table
        assertRow(arrayListOf(0, -1, -2, -3), table!![0])
        assertRow(arrayListOf(-1, -2, -1, -2), table!![1])
        assertRow(arrayListOf(-2, -3, -2, -1), table!![2])
        // assert alignment using alignment table
        val t = AbstractTest.table(graph)
        assertEquals("|a|b|c|", AbstractTest.toString(t, w0))
        assertEquals("| |b|c|", AbstractTest.toString(t, w1))
    }

    @Test fun testSuperbase() {
        val w = createWitnesses("X a b c d e f X g h i Y Z j k", "a b c Y d e f Y Z g h i X j k")
        val witnesses = ArrayList<SimpleWitness>()
        witnesses.addAll(w)
        val algorithm = EditGraphAligner()
        val collation = SimpleCollation(witnesses, algorithm, false)
        val graph = VariantGraph()
        collation.collate(graph)
        val superbase = algorithm.createSuperbaseFromVariantGraph(graph)
        //TODO: I need to test this with a reg exp!
        //assertSuperbaseEquals("X a b c Y d e f X Y Z g h i Y Z X j k", superbase)
    }


}