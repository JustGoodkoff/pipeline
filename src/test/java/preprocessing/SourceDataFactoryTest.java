package preprocessing;

import com.nsu.preprocessing.model.GraphDefinition;
import com.nsu.preprocessing.model.GraphDefinition.SourceDef;
import com.nsu.preprocessing.model.GraphDefinition.SourceItemDef;
import com.nsu.preprocessing.model.GraphDefinition.TypeDef;
import com.nsu.runtime.model.factory.SourceDataFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SourceDataFactoryTest {

    public static class TestPojo {
        public String id;

        public TestPojo() {
        }

        public TestPojo(String id) {
            this.id = id;
        }
    }

    @Test
    void emptySources_returnsEmptyMap() {
        GraphDefinition def = new GraphDefinition();
        def.types = Map.of();
        def.sources = null;

        Map<String, List<Object>> result = SourceDataFactory.build(def);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void singleSource_singleItem_parsedCorrectly() {
        TypeDef type = new TypeDef();
        type.javaType = TestPojo.class.getName();

        SourceItemDef item = new SourceItemDef();
        item.type = "TestPojo";
        item.value = Map.of("id", "x1");

        SourceDef source = new SourceDef();
        source.data = List.of(item);

        GraphDefinition def = new GraphDefinition();
        def.types = Map.of("TestPojo", type);
        def.sources = Map.of("source", source);

        Map<String, List<Object>> result = SourceDataFactory.build(def);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("source"));
        assertEquals(1, result.get("source").size());

        Object obj = result.get("source").get(0);
        assertTrue(obj instanceof TestPojo);
        assertEquals("x1", ((TestPojo) obj).id);
    }

    @Test
    void multipleItems_preserveOrder() {
        TypeDef type = new TypeDef();
        type.javaType = TestPojo.class.getName();

        SourceItemDef i1 = new SourceItemDef();
        i1.type = "TestPojo";
        i1.value = Map.of("id", "a");

        SourceItemDef i2 = new SourceItemDef();
        i2.type = "TestPojo";
        i2.value = Map.of("id", "b");

        SourceDef source = new SourceDef();
        source.data = List.of(i1, i2);

        GraphDefinition def = new GraphDefinition();
        def.types = Map.of("TestPojo", type);
        def.sources = Map.of("src", source);

        Map<String, List<Object>> result = SourceDataFactory.build(def);

        List<Object> list = result.get("src");
        assertEquals(2, list.size());
        assertEquals("a", ((TestPojo) list.get(0)).id);
        assertEquals("b", ((TestPojo) list.get(1)).id);
    }

    @Test
    void emptyValue_usesDefaultConstructor() {
        TypeDef type = new TypeDef();
        type.javaType = TestPojo.class.getName();

        SourceItemDef item = new SourceItemDef();
        item.type = "TestPojo";
        item.value = Map.of();

        SourceDef source = new SourceDef();
        source.data = List.of(item);

        GraphDefinition def = new GraphDefinition();
        def.types = Map.of("TestPojo", type);
        def.sources = Map.of("src", source);

        Map<String, List<Object>> result = SourceDataFactory.build(def);

        TestPojo pojo = (TestPojo) result.get("src").get(0);
        assertNull(pojo.id);
    }

    @Test
    void unknownType_throws() {
        SourceItemDef item = new SourceItemDef();
        item.type = "Missing";
        item.value = Map.of("x", 1);

        SourceDef source = new SourceDef();
        source.data = List.of(item);

        GraphDefinition def = new GraphDefinition();
        def.types = Map.of();
        def.sources = Map.of("src", source);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> SourceDataFactory.build(def)
        );

        assertTrue(ex.getCause().getMessage().contains("Unknown type"));
    }
}
