package eu.interedition.collatex.dekker.editgraphaligner

import eu.interedition.collatex.CollationAlgorithm
import eu.interedition.collatex.Token
import eu.interedition.collatex.VariantGraph
import java.util.*

/**
 * Created by ronalddekker on 24/09/15.
 */
public class EditGraphAligner : CollationAlgorithm.Base() {
    public var table: EditGraphTable? = null


    override fun collate(against: VariantGraph?, witness: MutableIterable<Token>?) {
        // this is not possible, since this aligner needs to have an index of the whole witness set and a super base structure
        throw UnsupportedOperationException()
    }

    override fun collate(against: VariantGraph, witnesses: List<Iterable<Token>>) {
        // merge the tokens of the first witness into the variant graph
        val tokens = witnesses[0]
        this.merge(against, tokens, Collections.emptyMap())

        // create superbase from the tokens of the first witness
        val superbase = ArrayList<Token>()
        superbase.addAll(tokens)

        // TODO: here I might need to build a token to vertex map

        // now process the second till last witnesses
        for (x in 1..witnesses.size()-1) {
            val nextWitness = witnesses[x]

            // prepare the scorer for the next witness
            // TODO

            // align tokens of the next witness against the superbase
            // TODO: add token to vertex map here as third parameter
            val alignment = alignTable(superbase, nextWitness)
        }
    }

    private fun alignTable(superbase: List<Token>, witness: Iterable<Token>): Unit {
        // convert witness tokens into a list, not efficient
        // but we need to traverse them many times (unless we are going to use integers are pointers to tokens in the token array in the future)
        val witnessTokens = ArrayList<Token>()
        witnessTokens.addAll(witness)

        createTableAndScoreCells(superbase, witnessTokens)
    }


    private fun createTableAndScoreCells(superbase: List<Token>, witnessTokens: List<Token>) {
        // here begins the meat that we want to test..
        // we need to create table object that is size superbase +1 x length (witness) +1
        // note that the array is empty
        val n = superbase.size() + 1
        val m = witnessTokens.size() + 1
        this.table = EditGraphTable(n, m)
        table?.align()
    }

}