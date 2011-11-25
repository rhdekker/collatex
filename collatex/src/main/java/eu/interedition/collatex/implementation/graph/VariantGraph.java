/**
 * CollateX - a Java library for collating textual sources,
 * for example, to produce an apparatus.
 *
 * Copyright (C) 2010 ESF COST Action "Interedition".
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.interedition.collatex.implementation.graph;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.interedition.collatex.interfaces.*;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import java.util.*;

import static org.jgrapht.alg.BellmanFordShortestPath.findPathBetween;

// This class implements the IVariantGraph interface.
// The IVariantGraph interface is an extension of the DiGraph interface
// The implementation is based on a DAG.
// The VariantGraph contains a start and an end vertex.
// The VariantGraph contains a List of witnesses that have
// been added to the Graph.
@SuppressWarnings("serial")
public class VariantGraph extends DirectedAcyclicGraph<IVariantGraphVertex, IVariantGraphEdge> implements IVariantGraph {
  private final IVariantGraphVertex startVertex;
  private final IVariantGraphVertex endVertex;

  public VariantGraph() {
    super(IVariantGraphEdge.class);
    startVertex = new VariantGraphVertex("#", null);
    addVertex(startVertex);
    endVertex = new VariantGraphVertex("#", null);
    addVertex(endVertex);
  }

  @Override
  public IVariantGraphVertex getEndVertex() {
    return endVertex;
  }

  @Override
  public IVariantGraphVertex getStartVertex() {
    return startVertex;
  }

  @Override
  public List<IWitness> getWitnesses() {
    Set<IVariantGraphEdge> outgoingEdges = outgoingEdgesOf(startVertex);
    List<IWitness> totalWitnesses = Lists.newArrayList();
    for (IVariantGraphEdge edge : outgoingEdges) {
      totalWitnesses.addAll(edge.getWitnesses());
    }
    //NOTE: The set of outgoingEdges is unordered!
    //NOTE: That is unexpected behavior so the list of witnesses
    //NOTE: is sorted here! WOULD HAVE: insert order
    Collections.sort(totalWitnesses, new Comparator<IWitness>() {
      @Override
      public int compare(IWitness arg0, IWitness arg1) {
        return arg0.getSigil().compareTo(arg1.getSigil());
      } });
    return Collections.unmodifiableList(totalWitnesses);
  }

  @Override
  public boolean isEmpty() {
    return getWitnesses().isEmpty();
  }

  @Override
  public List<IVariantGraphEdge> getPath(IWitness witness) {
    List<IVariantGraphEdge> path = Lists.newArrayList();
    IVariantGraphVertex currentVertex = getStartVertex();
    while (currentVertex != getEndVertex()) {
      Set<IVariantGraphEdge> outgoingEdges = outgoingEdgesOf(currentVertex);
      boolean found = false;
      for (IVariantGraphEdge edge : outgoingEdges) {
        if (!found && edge.containsWitness(witness)) {
          found = true;
          path.add(edge);
          currentVertex = getEdgeTarget(edge);
          continue;
        }
      }
      if (!found) {
        throw new RuntimeException("No valid path found for " + witness.getSigil());
      }
    }
    return path;
  }

  /**
   * @return
   * @todo Should getLongestPath() method return IVariantGraphEdges?
   */
  public List<IVariantGraphVertex> getLongestPath() {
    for (IVariantGraphEdge edge : edgeSet()) {
      // Weights are set to negative value to generate the longest path instead of the shortest path
      setEdgeWeight(edge, -1);
    }
    final IVariantGraphVertex startVertex = getStartVertex();
    final IVariantGraphVertex endVertex = getEndVertex();
    final List<IVariantGraphVertex> vertices = Lists.newArrayList();
    for (IVariantGraphEdge edge : findPathBetween(this, startVertex, endVertex)) {
      IVariantGraphVertex edgeTarget = getEdgeTarget(edge);
      if (edgeTarget != endVertex) {
        vertices.add(edgeTarget);
      }
    }
    return vertices;
  }


  @Override
  public List<INormalizedToken> getTokens(IWitness witness) {
    List<IVariantGraphEdge> edges = getPath(witness);
    List<INormalizedToken> tokens = Lists.newArrayList();
    for (IVariantGraphEdge edge : edges) {
      IVariantGraphVertex vertex = getEdgeTarget(edge);
      if (vertex != getEndVertex()) {
        tokens.add(vertex);
      }  
    }
    return tokens;
  }

  @Override
  public boolean isNear(IToken a, IToken b) {
    // sanity check!
    if (!(a instanceof IVariantGraphVertex)) {
      throw new RuntimeException("IToken a is not of type IVariantGraphVertex!");
    }
    if (!(b instanceof IVariantGraphVertex)) {
      throw new RuntimeException("IToken b is not of type IVariantGraphVertex!");
    }
    //NOTE: Vertices A and B should be connected and the maximum distance should be 1
    boolean connected = containsEdge((IVariantGraphVertex) a, (IVariantGraphVertex) b);
    connected = connected && (outDegreeOf((IVariantGraphVertex)a) == 1 || inDegreeOf((IVariantGraphVertex)b) == 1);
    return connected;
  }

  @Override
  public Iterator<INormalizedToken> tokenIterator() {
    final Iterator<IVariantGraphVertex> verticesIterator = iterator();
    return new Iterator<INormalizedToken>() {
      @Override
      public boolean hasNext() {
        return verticesIterator.hasNext();
      }

      @Override
      public INormalizedToken next() {
        IVariantGraphVertex vertex = verticesIterator.next();
        return vertex;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  // The variant graph is acyclic by default.  Sometimes we want to know which
  // nodes on that graph are really the same (transposed) node as appears elsewhere.
  // This method returns a map containing all transposed nodes and, for each, the
  // node it is a transposed duplicate of.
  @Override
  public Map<IVariantGraphVertex, IVariantGraphVertex> getTransposedTokens() {
	  Map<INormalizedToken, IVariantGraphVertex> keyToken2Vertex = Maps.newHashMap();
	  Map<IVariantGraphVertex, IVariantGraphVertex> transposedNodes = Maps.newHashMap();

	  for (IVariantGraphVertex avgVertex : vertexSet()) {
		  if( avgVertex != getStartVertex() && avgVertex != getEndVertex()) {
			  INormalizedToken vertexKey = avgVertex.getVertexKey();
			  if (keyToken2Vertex.containsKey(vertexKey)) {
				  IVariantGraphVertex origVertex = keyToken2Vertex.get(vertexKey);
				  transposedNodes.put(avgVertex, origVertex);
			  } else {
				  keyToken2Vertex.put(vertexKey, avgVertex);
			  }
		  }
	  }
	  return transposedNodes;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).addValue(Iterables.toString(getWitnesses())).toString();
  }
}
