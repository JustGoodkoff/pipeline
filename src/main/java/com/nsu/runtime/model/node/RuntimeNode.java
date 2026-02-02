package com.nsu.runtime.model.node;

import com.nsu.runtime.model.Channel;
import java.util.Map;

public abstract class RuntimeNode<I, O> implements Runnable {

    public final String id;
    protected final Map<String, Channel<I>> inputs;
    protected final Map<String, Channel<O>> outputs;

    protected RuntimeNode(
            String id,
            Map<String, Channel<I>> inputs,
            Map<String, Channel<O>> outputs
    ) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
    }
}

