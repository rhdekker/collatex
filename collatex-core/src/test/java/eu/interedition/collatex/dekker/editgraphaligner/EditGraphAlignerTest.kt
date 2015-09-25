package eu.interedition.collatex.dekker.editgraphaligner

import org.junit.After
import org.junit.Before
import org.junit.Test
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
}