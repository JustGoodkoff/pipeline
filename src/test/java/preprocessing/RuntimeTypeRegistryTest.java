package preprocessing;

import com.nsu.preprocessing.model.GraphDefinition.TypeDef;
import com.nsu.runtime.RuntimeTypeRegistry;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RuntimeTypeRegistryTest {

    @Test
    void constructor_validTypes_loadedSuccessfully() {
        TypeDef img = new TypeDef();
        img.javaType = "java.lang.String";

        TypeDef num = new TypeDef();
        num.javaType = "java.lang.Integer";

        RuntimeTypeRegistry registry = new RuntimeTypeRegistry(
                Map.of(
                        "StringType", img,
                        "IntType", num
                )
        );

        assertEquals(String.class, registry.resolve("StringType"));
        assertEquals(Integer.class, registry.resolve("IntType"));
    }

    @Test
    void constructor_unknownClass_throws() {
        TypeDef bad = new TypeDef();
        bad.javaType = "no.such.Class";

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> new RuntimeTypeRegistry(
                        Map.of("Bad", bad)
                )
        );

        assertTrue(ex.getMessage().contains("Не найден javaType"));
    }

    @Test
    void resolve_unknownType_throws() {
        TypeDef img = new TypeDef();
        img.javaType = "java.lang.String";

        RuntimeTypeRegistry registry = new RuntimeTypeRegistry(
                Map.of("StringType", img)
        );

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> registry.resolve("Missing")
        );

        assertTrue(ex.getMessage().contains("Неизвестный тип"));
    }

    @Test
    void isAssignable_sameType_true() {
        TypeDef str = new TypeDef();
        str.javaType = "java.lang.String";

        RuntimeTypeRegistry registry = new RuntimeTypeRegistry(
                Map.of("A", str)
        );

        assertTrue(registry.isAssignable("A", "A"));
    }

    @Test
    void isAssignable_subclassToParent_true() {
        TypeDef number = new TypeDef();
        number.javaType = "java.lang.Number";

        TypeDef integer = new TypeDef();
        integer.javaType = "java.lang.Integer";

        RuntimeTypeRegistry registry = new RuntimeTypeRegistry(
                Map.of(
                        "Number", number,
                        "Integer", integer
                )
        );

        assertTrue(registry.isAssignable("Number", "Integer"));
    }

    @Test
    void isAssignable_parentToSubclass_false() {
        TypeDef number = new TypeDef();
        number.javaType = "java.lang.Number";

        TypeDef integer = new TypeDef();
        integer.javaType = "java.lang.Integer";

        RuntimeTypeRegistry registry = new RuntimeTypeRegistry(
                Map.of(
                        "Number", number,
                        "Integer", integer
                )
        );

        assertFalse(registry.isAssignable("Integer", "Number"));
    }
}
