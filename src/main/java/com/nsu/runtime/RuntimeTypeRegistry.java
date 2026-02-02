package com.nsu.runtime;

import com.nsu.preprocessing.model.GraphDefinition.TypeDef;
import java.util.HashMap;
import java.util.Map;

public final class RuntimeTypeRegistry {

    private final Map<String, Class<?>> types = new HashMap<>();

    public RuntimeTypeRegistry(Map<String, TypeDef> defs) {
        defs.forEach((name, def) -> {
            try {
                types.put(name, Class.forName(def.javaType));
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(
                        "Не найден javaType для " + name, e
                );
            }
        });
    }

    public Class<?> resolve(String type) {
        Class<?> cls = types.get(type);
        if (cls == null) {
            throw new IllegalStateException("Неизвестный тип: " + type);
        }
        return cls;
    }

    public boolean isAssignable(String from, String to) {
        return resolve(from).isAssignableFrom(resolve(to));
    }
}
