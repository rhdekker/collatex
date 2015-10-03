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
    val baseTokenToVertex = HashMap<Token, VariantGraph.Vertex>()

    override fun collate(against: VariantGraph?, witness: MutableIterable<Token>?) {
        // this is not possible, since this aligner needs to have an index of the whole witness set and a super base structure
        throw UnsupportedOperationException()
    }

    override fun collate(against: VariantGraph, witnesses: List<Iterable<Token>>) {
        // merge the tokens of the first witness into the variant graph
        val tokens = witnesses[0]
        this.merge(against, tokens, Collections.emptyMap())
        baseTokenToVertex.putAll(witnessTokenVertices)

        // create superbase from the tokens of the first witness
        var superbase = ArrayList<Token>()
        superbase.addAll(tokens)


        // now process the second till last witnesses
        for (x in 1..witnesses.size()-1) {
            val nextWitness = witnesses[x]

            // prepare the scorer for the next witness
            // TODO

            // align tokens of the next witness against the superbase
            val alignment = alignTable(superbase, nextWitness)

            // debug
            // println(alignment)

            // merge
            merge(against, nextWitness, alignment)
            baseTokenToVertex.putAll(witnessTokenVertices)

            superbase = createSuperbaseFromVariantGraph(against)

        }
    }

    // create new superbase from variant graph
    fun createSuperbaseFromVariantGraph(against: VariantGraph): ArrayList<Token> {
        // NOTE: this is not the ultimate solution, since there are multiple possible topologic orders possible
        val vertices = arrayListOf<VariantGraph.Vertex>()
        vertices.addAll(against.vertices())

        val superbase = arrayListOf<Token>()
        for (vertex in vertices) {
            val tokens = vertex.tokens().iterator()
            // we need to skip start and end vertices
            if (tokens.hasNext()) {
                superbase.add(tokens.next())
            }
        }
        return superbase
    }

    private fun alignTable(superbase: List<Token>, witness: Iterable<Token>): MutableMap<Token, VariantGraph.Vertex> {
        // convert witness tokens into a list, not efficient
        // but we need to traverse them many times (unless we are going to use integers are pointers to tokens in the token array in the future)
        val witnessTokens = ArrayList<Token>()
        witnessTokens.addAll(witness)
       return createTableAndScoreCells(superbase, witnessTokens)
    }


    private fun createTableAndScoreCells(superbase: List<Token>, witnessTokens: List<Token>): MutableMap<Token, VariantGraph.Vertex> {
        // here begins the meat that we want to test..

        // we need to create table object that is size superbase +1 x length (witness) +1
        // note that the array is empty
        val n = superbase.size() + 1
        val m = witnessTokens.size() + 1
        this.table = EditGraphTable(n, m, Scorer(superbase, witnessTokens))

        // align superbase and witness using the scorer
        // it also creates a new superbase during the alignment
        val tokenAlignment = table!!.align()

        // convert token token to token alignment into vertex to token alignment
        val alignment = hashMapOf<Token, VariantGraph.Vertex>()
        for (token in tokenAlignment.keySet()) {
            val alignedBaseToken = tokenAlignment[token]
            val vertex = baseTokenToVertex[alignedBaseToken]
            if (vertex == null || alignedBaseToken == null) {
                throw RuntimeException("vertex $vertex or aligned token $alignedBaseToken for token $token is unexpectedly null!")
            }
            alignment[token] = vertex
        }
        return alignment
    }
}