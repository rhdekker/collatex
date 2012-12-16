package eu.interedition.collatex.neo4j;

import eu.interedition.collatex.VariantGraphTransposition;
import org.neo4j.graphdb.Relationship;

import com.google.common.base.Function;
import com.google.common.base.Objects;

/**
 * @author <a href="http://gregor.middell.net/" title="Homepage">Gregor Middell</a>
 */
public class Neo4jVariantGraphTransposition implements VariantGraphTransposition {

  private static final String TRANSPOSITIONID_KEY = "transpositionId";
  private final Neo4jVariantGraph graph;
  private final Relationship relationship;

  public Neo4jVariantGraphTransposition(Neo4jVariantGraph graph, Relationship relationship, int transpositionId) {
    this.graph = graph;
    this.relationship = relationship;
    this.relationship.setProperty(TRANSPOSITIONID_KEY, transpositionId);
  }

  public Neo4jVariantGraphTransposition(Neo4jVariantGraph graph, Relationship relationship) {
    this.graph = graph;
    this.relationship = relationship;
  }

  public Neo4jVariantGraphTransposition(Neo4jVariantGraph graph, Neo4jVariantGraphVertex from, Neo4jVariantGraphVertex to, int transpositionId) {
    this(graph, from.getNode().createRelationshipTo(to.getNode(), GraphRelationshipType.TRANSPOSITION), transpositionId);
  }

  @Override
  public Neo4jVariantGraphVertex from() {
    return new Neo4jVariantGraphVertex(graph, relationship.getStartNode());
  }

  @Override
  public Neo4jVariantGraphVertex to() {
    return new Neo4jVariantGraphVertex(graph, relationship.getEndNode());
  }

  @Override
  public Neo4jVariantGraphVertex other(Neo4jVariantGraphVertex vertex) {
    return new Neo4jVariantGraphVertex(graph, relationship.getOtherNode(vertex.getNode()));
  }

  @Override
  public void delete() {
    relationship.delete();
  }

  @Override
  public int hashCode() {
    return relationship.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null && obj instanceof VariantGraphTransposition) {
      return relationship.equals(((Neo4jVariantGraphTransposition) obj).relationship);
    }
    return super.equals(obj);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).addValue(from()).addValue(to()).toString();
  }

  public static Function<Relationship, VariantGraphTransposition> createWrapper(final Neo4jVariantGraph in) {
    return new Function<Relationship, VariantGraphTransposition>() {
      @Override
      public VariantGraphTransposition apply(Relationship input) {
        return new Neo4jVariantGraphTransposition(in, input);
      }
    };
  }

  @Override
  public int getId() {
    return (Integer) relationship.getProperty(TRANSPOSITIONID_KEY, 0);
  }

}
