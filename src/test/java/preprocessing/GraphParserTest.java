package preprocessing;

import com.nsu.preprocessing.GraphParser;
import com.nsu.preprocessing.model.GraphDefinition;
import com.nsu.preprocessing.model.PortDef;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class GraphParserTest {

    @Test
    void parse_validPipelineJson_doesNotThrow() {
        File file = new File(
                "src/main/resources/pipeline.json"
        );

        assertDoesNotThrow(() -> {
            GraphDefinition def = GraphParser.parse(file);
            assertNotNull(def);
        });
    }

    @Test
    void parse_types_areParsedCorrectly() {
        GraphDefinition def = GraphParser.parse(
                new File("src/main/resources/pipeline.json")
        );

        assertNotNull(def.types);
        assertEquals(2, def.types.size());

        GraphDefinition.TypeDef imageMeta = def.types.get("ImageMeta");
        assertNotNull(imageMeta);
        assertEquals("com.nsu.data.ImageMeta", imageMeta.javaType);
        assertNull(imageMeta.extendsType);

        GraphDefinition.TypeDef jpgImage = def.types.get("JpgImage");
        assertNotNull(jpgImage);
        assertEquals("ImageMeta", jpgImage.extendsType);
        assertEquals("com.nsu.data.JpgImage", jpgImage.javaType);
    }

    @Test
    void parse_sources_areParsedCorrectly() {
        GraphDefinition def = GraphParser.parse(
                new File("src/main/resources/pipeline.json")
        );

        assertNotNull(def.sources);
        assertEquals(1, def.sources.size());

        GraphDefinition.SourceDef source = def.sources.get("source");
        assertNotNull(source);
        assertNotNull(source.data);
        assertEquals(3, source.data.size());

        GraphDefinition.SourceItemDef first = source.data.get(0);
        assertEquals("ImageMeta", first.type);
        assertNotNull(first.value);
        assertEquals("img-1", first.value.get("id"));
    }

    @Test
    void parse_operators_structureIsCorrect() {
        GraphDefinition def = GraphParser.parse(
                new File("src/main/resources/pipeline.json")
        );

        assertNotNull(def.operators);
        assertEquals(3, def.operators.size());

        GraphDefinition.OperatorDef source = def.operators.get("source");
        assertEquals("source", source.kind);
        assertNull(source.inputs);
        assertNotNull(source.outputs);
        assertTrue(source.outputs.containsKey("out"));

        GraphDefinition.OperatorDef denoise = def.operators.get("denoise");
        assertEquals("map", denoise.kind);
        assertNotNull(denoise.funBody);
        assertTrue(denoise.funBody.contains("new com.nsu.data.JpgImage"));

        assertNotNull(denoise.inputs);
        assertNotNull(denoise.outputs);
    }

    @Test
    void parse_ports_haveCorrectLinks() {
        GraphDefinition def = GraphParser.parse(
                new File("src/main/resources/pipeline.json")
        );

        GraphDefinition.OperatorDef denoise = def.operators.get("denoise");

        PortDef in = denoise.inputs.get("in");
        assertEquals("ImageMeta", in.type);
        assertEquals("source.out", in.from);

        PortDef out = denoise.outputs.get("out");
        assertEquals("JpgImage", out.type);
        assertEquals("sink.in", out.to);
    }
}
