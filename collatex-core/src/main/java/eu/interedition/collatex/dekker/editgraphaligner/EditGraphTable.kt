package eu.interedition.collatex.dekker.editgraphaligner

import eu.interedition.collatex.Token
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

    fun align() {
        // debug
        println("length: "+cells.size())
        println("width: "+cells[0].size())

        // we need to traverse the array diagonally and score each cell
        cells[0][0] = TableCell(0)

        traverseDiagonallyAndScore()
    }

    class TableCell(i: Int) {
        // g stands for the number of gaps encountered
        var g = 0
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
    fun score_cell(y: Int, x: Int, parent: EditGraphTable.TableCell?, editoperation: Int): EditGraphTable.TableCell {
        // no matching is possible in this case (always treated as a gap)
        // it is either an add of a delete
        if (x == 0 || y == 0) {
            return EditGraphTable.TableCell(parent!!.g - 1)
        }



        println("$y;$x")


        // fetch tokens when needed...
//        println(superbase)
//        println(witness)
        val b = superbase[x-1]
        val w = witness[y-1]
        return EditGraphTable.TableCell(0)
    }

//    # no matching possible in this case (always treated as a gap)
//    # it is either an add or a delete
//    if x == 0 or y == 0:
//    table_node.g = parent_node.g - 1
//    return
//
//    # it is either an add/delete or replacement (so an add and a delete)
//    # it is a replacement
//    if edit_operation == 0:
//    match = self.match_function(token_a, token_b)
//    #             print("testing "+token_a.token_string+" and "+token_b.token_string+" "+str(match))
//    # match = token_a.token_string == token_b.token_string
//    # based on match or not and parent_node calculate new score
//    if match==0:
//    # mark the fact that this node is match
//    table_node.match = True
//    # do not change score for now
//    table_node.g = parent_node.g
//    # count segments
//    if parent_node.match == False:
//    table_node.segments = parent_node.segments + 1
//    return
//    if match==1:
//    table_node.g = parent_node.g - 0.5 #TODO: TEST TEST TEST
//    pass
//    else:
//    table_node.g = parent_node.g - 2
//    return
//    # it is an add/delete
//    else:
//    table_node.g = parent_node.g - 1
//    return


}
