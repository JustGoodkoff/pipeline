package runtime;

import static org.junit.jupiter.api.Assertions.*;

import com.nsu.preprocessing.GraphBuilder;
import com.nsu.preprocessing.GraphParser;
import com.nsu.preprocessing.model.ExecutionGraph;
import com.nsu.preprocessing.model.GraphDefinition;
import com.nsu.runtime.RuntimeBuilder;
import com.nsu.runtime.RuntimeGraph;
import com.nsu.runtime.RuntimeTypeRegistry;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RuntimeBuilderTest {

    private GraphDefinition loadDefinition() throws Exception {
        return GraphParser.parse(
                new File(
                        "E:\\Users\\Dmitriy\\IntelliJIDEAProjects\\StreamingR\\src\\main\\resources\\pipeline.json"
                )
        );
    }

    @Test
    void build_runtimeGraph_fromJson() throws Exception {
        GraphDefinition def = loadDefinition();

        ExecutionGraph exec = GraphBuilder.build(def);
        RuntimeTypeRegistry registry = new RuntimeTypeRegistry(def.types);

        Map<String, List<Object>> sourceData =
                com.nsu.runtime.model.factory.SourceDataFactory.build(def);

        RuntimeGraph graph =
                RuntimeBuilder.build(exec, registry, sourceData);

        assertNotNull(graph);
    }

    @Test
    void runtimeGraph_containsAllOperators() throws Exception {
        GraphDefinition def = loadDefinition();

        ExecutionGraph exec = GraphBuilder.build(def);
        RuntimeTypeRegistry registry = new RuntimeTypeRegistry(def.types);

        RuntimeGraph graph =
                RuntimeBuilder.build(
                        exec,
                        registry,
                        com.nsu.runtime.model.factory.SourceDataFactory.build(def)
                );

        assertEquals(
                def.operators.size(),
                graph.getNodes().size()
        );
    }

    @Test
    void runtimeGraph_startAndJoin() throws Exception {
        GraphDefinition def = loadDefinition();

        ExecutionGraph exec = GraphBuilder.build(def);
        RuntimeTypeRegistry registry = new RuntimeTypeRegistry(def.types);

        RuntimeGraph graph =
                RuntimeBuilder.build(
                        exec,
                        registry,
                        com.nsu.runtime.model.factory.SourceDataFactory.build(def)
                );

        assertDoesNotThrow(() -> {
            graph.start();
            graph.join();
        });
    }
}

