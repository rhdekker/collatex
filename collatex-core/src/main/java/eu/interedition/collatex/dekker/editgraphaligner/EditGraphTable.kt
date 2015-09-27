package eu.interedition.collatex.dekker.editgraphaligner

/**
 * Created by ronalddekker on 27/09/15.
 *
 * Class represents a table representation of an edit graph.
 */

// n is the width, m is the length
class EditGraphTable(val n: Int, val m: Int) {
    private var cells: Array<Array<TableCell?>> = Array(m, { arrayOfNulls<TableCell>(n) })

    public fun get(index: Int): Array<TableCell?> {
        return cells[index]
    }

    fun align() {
        // debug
        println("length: "+cells.size())
        println("width: "+cells[0].size())

        // we need to traverse the array diagonally and score each cell
        cells[0][0] = TableCell()

        traverseDiagonallyAndScore()
    }

    class TableCell() {
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
        println("$y, $x")
        cells[y][x]=TableCell()
    }
}