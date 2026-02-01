package com.nsu.runtime.model.factory;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.nsu.preprocessing.model.GraphDefinition;
import com.nsu.preprocessing.model.GraphDefinition.SourceDef;
import com.nsu.preprocessing.model.GraphDefinition.SourceItemDef;
import com.nsu.preprocessing.model.GraphDefinition.TypeDef;
import java.lang.reflect.Constructor;
import java.util.*;

public final class SourceDataFactory {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, List<Object>> build(GraphDefinition def) {
        Map<String, List<Object>> result = new HashMap<>();

        if (def.sources == null) {
            return result;
        }

        for (Map.Entry<String, SourceDef> entry : def.sources.entrySet()) {
            String sourceId = entry.getKey();
            SourceDef src = entry.getValue();

            List<Object> objects = new ArrayList<>();
            for (SourceItemDef item : src.data) {
                objects.add(instantiate(def, item));
            }
            result.put(sourceId, objects);
        }
        return result;
    }

    private static Object instantiate(GraphDefinition def, SourceItemDef item) {
        try {
            TypeDef typeDef = def.types.get(item.type);
            if (typeDef == null || typeDef.javaType == null) {
                throw new IllegalStateException("Unknown type: " + item.type);
            }

            Class<?> cls = Class.forName(typeDef.javaType);

            if (item.value == null || item.value.isEmpty()) {
                Constructor<?> ctor = cls.getDeclaredConstructor();
                ctor.setAccessible(true);
                return ctor.newInstance();
            }

            return mapper.convertValue(item.value, cls);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
