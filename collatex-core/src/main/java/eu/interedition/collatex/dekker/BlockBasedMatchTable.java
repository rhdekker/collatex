package eu.interedition.collatex.dekker;

import eu.interedition.collatex.Token;
import eu.interedition.collatex.VariantGraph;
import eu.interedition.collatex.Witness;
import eu.interedition.collatex.dekker.matrix.Coordinate;
import eu.interedition.collatex.dekker.matrix.Island;
import eu.interedition.collatex.dekker.matrix.MatchTable;
import eu.interedition.collatex.simple.SimpleWitness;
import eu.interedition.collatex.util.VariantGraphRanking;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Created by ronald on 4/26/15.
 */
public class BlockBasedMatchTable implements MatchTable {
    private final List<Island> islands;
    // NOTE: The table is sparse; so this is not the most efficient implementation.
    // Note: In theory would could move all the lookup functionality to the islands
    // Note: This would however introduce API changes
    private final MatchTableCell[][] table;
    private final Token[] witness;
    private final int[] ranks;

    private BlockBasedMatchTable(List<Island> islands, Token[] tokens, int[] ranks) {
        this.islands = islands;
        this.table = new MatchTableCell[tokens.length][ranks.length];
        this.witness = tokens;
        this.ranks = ranks;
    }

    public static MatchTable create(Dekker21Aligner aligner, VariantGraph graph, SimpleWitness witness) {
        return createMatchTable(aligner, graph, witness);
    }

    public static MatchTable createMatchTable(Dekker21Aligner aligner, VariantGraph g, Witness w) {
        // we need the variant graph ranking for the projection in the vector space
        VariantGraphRanking ranking = VariantGraphRanking.of(g);
        // result
        List<Island> result = new ArrayList<>();
        // witness tokens
        Token[] tokens = StreamSupport.stream(((Iterable<Token>)w).spliterator(), false).toArray(Token[]::new);
        // graph ranks
        int[] ranks = IntStream.range(0, Math.max(0, ranking.apply(g.getEnd()) - 1)).toArray();
        // create table
        BlockBasedMatchTable table = new BlockBasedMatchTable(result, tokens, ranks);
        // based on the TokenIndex we build up the islands...
        // an island is a graph instance and a witness instance of the same block combined
        List<Block.Instance> instances = aligner.tokenIndex.getBlockInstancesForWitness(w);
        // we have to combine each instance in the witness with the other instances already present in the graph
        for (Block.Instance witnessInstance : instances) {
            // fetch block
            Block block = witnessInstance.block;
            List<Block.Instance> allInstances = block.getAllInstances();
            // calc graph Instances
            List<Block.Instance> graphInstances = allInstances.stream().filter(instance -> instance.start_token != witnessInstance.start_token).collect(Collectors.toList());
            for (Block.Instance graphInstance : graphInstances) {
                // combine witness and graph instance into an island
                // project graph instance into vectorspace
                int start_token = graphInstance.start_token;
                VariantGraph.Vertex v = aligner.vertex_array[start_token];
                if (v==null) {
                    throw new RuntimeException("Vertex is null!");
                }
                Integer column = ranking.apply(v)-1;
                int row = witnessInstance.start_token - aligner.tokenIndex.getStartTokenPositionForWitness(w);
                Coordinate startCoordinate = new Coordinate(row, column);
                Coordinate endCoordinate = new Coordinate(row+block.length-1, column+block.length-1);
                Island island = new Island(startCoordinate, endCoordinate);
                result.add(island);
                // set first token, vertex
                // TODO: add the other tokens..
                table.set(row, column, aligner.tokenIndex.token_array.get(start_token), v);
            }
        }
        return table;
    }

    @Override
    public VariantGraph.Vertex vertexAt(int rowIndex, int columnIndex) {
        return cell(rowIndex, columnIndex).map(c -> c.vertex).orElse(null);
    }

    @Override
    public Token tokenAt(int rowIndex, int columnIndex) {
        return cell(rowIndex, columnIndex).map(c -> c.token).orElse(null);
    }

    private Optional<MatchTableCell> cell(int rowIndex, int columnIndex) {
        return Optional.ofNullable(table[rowIndex][columnIndex]);
    }

    private void set(int rowIndex, int columnIndex, Token token, VariantGraph.Vertex vertex) {
        table[rowIndex][columnIndex] = new MatchTableCell(token, vertex);
    }

    @Override
    public List<Token> rowList() {
        return null;
    }

    @Override
    public List<Integer> columnList() {
        return null;
    }

    @Override
    public Set<Island> getIslands() {
        return new HashSet<>(islands);
    }

    private class MatchTableCell {
        public final Token token;
        public final VariantGraph.Vertex vertex;

        public MatchTableCell(Token token, VariantGraph.Vertex vertex) {
            this.token = token;
            this.vertex = vertex;
        }
    }
}
