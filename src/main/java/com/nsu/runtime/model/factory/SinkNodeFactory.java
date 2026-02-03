package com.nsu.runtime.model.factory;

import com.nsu.preprocessing.model.ExecutionGraph.ExecNode;
import com.nsu.runtime.model.Channel;
import com.nsu.runtime.model.node.RuntimeNode;
import com.nsu.runtime.model.node.SinkNode;
import java.util.List;
import java.util.Map;

public final class SinkNodeFactory<T> implements RuntimeNodeFactory {

    private final Class<T> inType;

    public SinkNodeFactory(Class<T> inType) {
        this.inType = inType;
    }

    @Override
    public String kind() {
        return "sink";
    }

    @Override
    public boolean supports(
            Map<String, Class<?>> inputTypes,
            Map<String, Class<?>> outputTypes
    ) {
        // Sink ожидает ровно один вход с типом assignable к inType
        // и вообще не имеет выходов
        return inputTypes.size() == 1
                && inputTypes.values().stream().allMatch(inType::isAssignableFrom)
                && outputTypes.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public RuntimeNode<T, Void> create(
            ExecNode node,
            Map<String, Channel<?>> inputs,
            Map<String, Channel<?>> outputs,
            List<Object> sourceData
    ) {
        Channel<T> in = (Channel<T>) inputs.values().iterator().next();
        return new SinkNode<>(node.id, in);
    }
}

