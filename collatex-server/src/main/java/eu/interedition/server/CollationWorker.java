package eu.interedition.server;

import eu.interedition.collatex.graph.GraphFactory;
import eu.interedition.collatex.graph.VariantGraph;
import eu.interedition.collatex.simple.SimpleVariantGraphSerializer;
import eu.interedition.server.collatex.Collation;
import eu.interedition.server.io.JacksonConverter;
import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author <a href="http://gregor.middell.net/" title="Homepage">Gregor Middell</a>
 */
@Component
public class CollationWorker implements GearmanFunction {

  private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();

  @Autowired
  private JacksonConverter jacksonConverter;

  @Autowired
  private GraphFactory graphFactory;

  @Transactional
  @Override
  public byte[] work(String function, byte[] data, GearmanFunctionCallback callback) throws Exception {
    final Collation collation = jacksonConverter.toObject(new InputRepresentation(new ByteArrayInputStream(data), MediaType.APPLICATION_JSON), Collation.class, null);
    // create
    final VariantGraph graph = graphFactory.newVariantGraph();

    if (collation != null) {
      // merge
      collation.getAlgorithm().collate(graph, collation.getWitnesses());

      // post-process
      if (collation.isJoined()) {
        graph.join();
      }
      graph.rank();
    }

    final ByteArrayOutputStream result = new ByteArrayOutputStream();
    XMLStreamWriter xml = null;
    try {
      new SimpleVariantGraphSerializer(graph).toGraphML(xml = XML_OUTPUT_FACTORY.createXMLStreamWriter(result));
    } catch (XMLStreamException e) {
      throw new IOException(e.getMessage(), e);
    } finally {
      try {
        xml.close();
      } catch (XMLStreamException e) {
      }
    }
    return result.toByteArray();
  }
}
