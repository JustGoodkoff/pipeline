package com.nsu.runtime.model.factory;

import com.nsu.preprocessing.model.ExecutionGraph.ExecNode;
import com.nsu.runtime.model.Channel;
import com.nsu.runtime.model.node.RuntimeNode;
import com.nsu.runtime.model.node.SourceNode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class SourceNodeFactory<T> implements RuntimeNodeFactory {

    private final Class<T> outType;

    public SourceNodeFactory(Class<T> outType) {
        this.outType = outType;
    }

    @Override
    public String kind() {
        return "source";
    }

    @Override
    public boolean supports(
            Map<String, Class<?>> inputTypes,
            Map<String, Class<?>> outputTypes
    ) {
        return inputTypes.isEmpty()
                && outputTypes.size() == 1
                && outputTypes.values().stream().allMatch(outType::isAssignableFrom);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RuntimeNode<Void, T> create(
            ExecNode node,
            Map<String, Channel<?>> inputs,
            Map<String, Channel<?>> outputs,
            List<Object> sourceData
    ) {
        Channel<T> out = (Channel<T>) outputs.values().iterator().next();
        Iterator<T> data;
        if (sourceData == null) {
            data = Collections.emptyIterator();
        } else {
            data = (Iterator<T>) sourceData.iterator();
        }
        return new SourceNode<>(node.id, out, data);
    }
}

