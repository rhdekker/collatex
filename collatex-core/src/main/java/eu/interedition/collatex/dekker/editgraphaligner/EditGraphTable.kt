package eu.interedition.collatex.dekker.editgraphaligner

import eu.interedition.collatex.Token
import eu.interedition.collatex.simple.SimpleToken
import java.util.*

/**
 * Created by ronalddekker on 27/09/15.
 *
 * Class represents a table representation of an edit graph.
 */

// n is the width, m is the length
class EditGraphTable(val n: Int, val m: Int, val scorer: Scorer) {
    private var cells: Array<Array<TableCell?>> = Array(m, { arrayOfNulls<TableCell>(n) })

    public fun get(index: Int): Array<TableCell?> {
        return cells[index]
    }

    fun align(): HashMap<Token, Token> {
        // debug
//        println("length: "+cells.size())
//        println("width: "+cells[0].size())

        // we need to traverse the array diagonally and score each cell
        cells[0][0] = TableCell(0)

        traverseDiagonallyAndScore()

        val alignment = getAlignmentFromTable()
        return alignment
    }


    fun getAlignmentFromTable(): HashMap<Token, Token> {
        val alignment = HashMap<Token, Token>()

        // segment stuff
        // note we traverse from right to left!
        val last_x = n
        val last_y = m
        val newSuperbase = ArrayList<Token>()

        // start lower right cell
        var x = n-1
        var y = m-1
        // work our way to the upper left
        while (x > 0 && y > 0) {
            processCell(alignment, y, x)
            // examine neighbor nodes
            val nodesToExamine = HashSet<TableCell?>()
            nodesToExamine.add(cells[y][x-1])
            nodesToExamine.add(cells[y-1][x])
            nodesToExamine.add(cells[y-1][x-1])
            // calculate the maximum scoring parent node
            val parentNode = Collections.max(nodesToExamine, TableCellComparator())
            // move position
            if (cells[y-1][x-1] == parentNode) {
                // another match or replacement
                y -= 1
                x -= 1
            } else {
                if (cells[y-1][x] == parentNode) {
                    // omission
                    y -= 1
                } else {
                    if (cells[y][x-1] == parentNode) {
                        // addition
                        x -= 1
                    }
                }
            }
        }
        // process additions/omissions in the begin of the superbase/witness
        // addToSuperbase(0, 0)
        return alignment
    }





    private fun processCell(alignment: HashMap<Token, Token>, y: Int, x: Int) {
        if (cells[y][x]!!.match==true) {
            val tokens = scorer.getTokens(y, x)
            alignment[tokens.second]=tokens.first
        }
    }

    // g stands for the number of gaps encountered
    class TableCell(val g: Int) {
        var match: Boolean = false
    }

    // This function traverses the table diagonally and scores each cell.
    // Original function from Mark Byers; translated from C into Kotlin.
    private fun traverseDiagonallyAndScore() {
        for (_slice in 0..m+n-1) {
            val z1 = if (_slice < n) 0 else _slice - n + 1
            val z2 = if (_slice < m) 0 else _slice - m + 1
            var j = _slice - z2
            while (j >= z1) {
                val x = _slice - j
                val y = j
                // this I would like to see with a call back
                this.scoreCell(y, x)
                j -= 1
            }
        }
    }



    private fun scoreCell(y: Int, x: Int) {
        // initialize root node score to zero (no edit operations have
        // been performed
        if (y == 0 && x == 0) {
            cells[y][x]=TableCell(0)
            return
        }
        // examine neighbor nodes
        val nodes_to_examine = HashSet<TableCell?>()
        // fetch existing score form the left node if possible
        if (x > 0) {
            nodes_to_examine.add(cells[y][x-1])
        }
        if (y > 0) {
            nodes_to_examine.add(cells[y-1][x])
        }
        if (x > 0 && y > 0) {
            nodes_to_examine.add(cells[y-1][x-1])
        }
        // calculate the maximum scoring parent node

        val nodesComparator = TableCellComparator()
        val max = Collections.max(nodes_to_examine, nodesComparator);

        val edit_operation = if (x > 0 && y > 0 && max == cells[y-1][x-1]) 0  else 1

        // fetch tokens? I would rather not to be honest
        val scored_cell = scorer.score_cell(y, x, max, edit_operation)
        cells[y][x] = scored_cell

    }






    class TableCellComparator : Comparator<TableCell?> {
        override fun compare(o1: TableCell?, o2: TableCell?): Int {
            return o1!!.g - o2!!.g
        }
    }
}
// voor nu geef ik aan de scorer de superbase en de witness tokens mee, liever zou ik zien
// dat daar de index meegegeven word...
class Scorer(val superbase: List<Token>, val witness: List<Token>) {
    fun getTokens(y: Int, x: Int): Pair<Token, Token> {
        val b = superbase[x - 1]
        val w = witness[y - 1]
        return Pair(b, w)
    }

    fun score_cell(y: Int, x: Int, parent: EditGraphTable.TableCell?, editOperation: Int): EditGraphTable.TableCell {
        // no matching is possible in this case (always treated as a gap)
        // it is either an add of a delete
        if (x == 0 || y == 0) {
            return EditGraphTable.TableCell(parent!!.g - 1)
        }

        // It is either an add/delete or match/replacement (so an add and a delete)
        // It is a replacement
        if (editOperation == 0) {
            // fetch tokens when needed...
            val b = superbase[x - 1]
            val w = witness[y - 1]
            val match = match(b, w)
            // based on match or not and parent_node calculate new score
            if (match) {
                // do not change score for now
                val table_node = EditGraphTable.TableCell(parent!!.g)
                table_node.match = true
                return table_node
            } else {
                val table_node = EditGraphTable.TableCell(parent!!.g - 2)
                return table_node
            }
        } else {
            // it is an add/delete
            val table_node = EditGraphTable.TableCell(parent!!.g - 1)
            return table_node
        }




        println("$y;$x")


//        println(superbase)
//        println(witness)
        return EditGraphTable.TableCell(0)
    }

    private fun match(b: Token, w: Token): Boolean {
        // I want to use the LCP intervals index here, but with two witnesses it does not matter
        if ((b as SimpleToken).normalized.equals((w as SimpleToken).normalized)) {
            return true
        }
        return false
    }
}
