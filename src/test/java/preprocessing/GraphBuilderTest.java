package preprocessing;

import com.nsu.preprocessing.GraphBuilder;
import com.nsu.preprocessing.model.ExecutionGraph;
import com.nsu.preprocessing.model.ExecutionGraph.ExecEdge;
import com.nsu.preprocessing.model.GraphDefinition;
import com.nsu.preprocessing.model.GraphDefinition.OperatorDef;
import com.nsu.preprocessing.model.GraphDefinition.TypeDef;
import com.nsu.preprocessing.model.PortDef;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GraphBuilderTest {

    private GraphDefinition baseDefinition() {
        GraphDefinition def = new GraphDefinition();

        TypeDef imageMeta = new TypeDef();
        imageMeta.javaType = "com.nsu.data.ImageMeta";

        TypeDef jpgImage = new TypeDef();
        jpgImage.extendsType = "ImageMeta";
        jpgImage.javaType = "com.nsu.data.JpgImage";

        def.types = Map.of(
                "ImageMeta", imageMeta,
                "JpgImage", jpgImage
        );

        return def;
    }

    @Test
    void build_validGraph_createsNodesAndEdges() {
        GraphDefinition def = baseDefinition();

        OperatorDef source = new OperatorDef();
        source.kind = "source";
        source.outputs = Map.of(
                "out", port(null, "ImageMeta", "denoise.in")
        );

        OperatorDef denoise = new OperatorDef();
        denoise.kind = "map";
        denoise.inputs = Map.of(
                "in", port("source.out", "ImageMeta", null)
        );
        denoise.outputs = Map.of(
                "out", port(null, "JpgImage", "sink.in")
        );

        OperatorDef sink = new OperatorDef();
        sink.kind = "sink";
        sink.inputs = Map.of(
                "in", port("denoise.out", "JpgImage", null)
        );

        def.operators = Map.of(
                "source", source,
                "denoise", denoise,
                "sink", sink
        );

        ExecutionGraph graph = GraphBuilder.build(def);

        assertEquals(3, graph.nodes.size());
        assertEquals(2, graph.edges.size());

        ExecEdge e1 = graph.edges.get(0);
        assertEquals("source", e1.fromNode.id);
        assertEquals("denoise", e1.toNode.id);

        ExecEdge e2 = graph.edges.get(1);
        assertEquals("denoise", e2.fromNode.id);
        assertEquals("sink", e2.toNode.id);
    }

    @Test
    void build_unknownTargetOperator_throws() {
        GraphDefinition def = baseDefinition();

        OperatorDef source = new OperatorDef();
        source.kind = "source";
        source.outputs = Map.of(
                "out", port(null, "ImageMeta", "missing.in")
        );

        def.operators = Map.of("source", source);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> GraphBuilder.build(def)
        );

        assertTrue(ex.getMessage().contains("Неизвестный оператор"));
    }

    @Test
    void build_unknownPort_throws() {
        GraphDefinition def = baseDefinition();

        OperatorDef a = new OperatorDef();
        a.kind = "source";
        a.outputs = Map.of(
                "out", port(null, "ImageMeta", "b.in")
        );

        OperatorDef b = new OperatorDef();
        b.kind = "sink";
        b.inputs = Map.of();

        def.operators = Map.of(
                "a", a,
                "b", b
        );

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> GraphBuilder.build(def)
        );

        assertTrue(ex.getMessage().contains("Неизвестный порт"));
    }

    @Test
    void build_incompatibleTypes_throws() {
        GraphDefinition def = baseDefinition();

        OperatorDef a = new OperatorDef();
        a.kind = "source";
        a.outputs = Map.of(
                "out", port(null, "JpgImage", "b.in")
        );

        OperatorDef b = new OperatorDef();
        b.kind = "sink";
        b.inputs = Map.of(
                "in", port("a.out", "ImageMeta", null)
        );

        def.operators = Map.of(
                "a", a,
                "b", b
        );

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> GraphBuilder.build(def)
        );

        assertTrue(ex.getMessage().contains("Несовместимые типы"));
    }

    @Test
    void build_invalidToFormat_throws() {
        GraphDefinition def = baseDefinition();

        OperatorDef a = new OperatorDef();
        a.kind = "source";
        a.outputs = Map.of(
                "out", port(null, "ImageMeta", "badformat")
        );

        def.operators = Map.of("a", a);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> GraphBuilder.build(def)
        );

        assertTrue(ex.getMessage().contains("Некорректная ссылка"));
    }

    private static PortDef port(String from, String type, String to) {
        PortDef p = new PortDef();
        p.from = from;
        p.to = to;
        p.type = type;
        return p;
    }
}
