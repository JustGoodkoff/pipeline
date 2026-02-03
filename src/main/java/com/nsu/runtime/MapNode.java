package com.nsu.runtime;

import com.nsu.runtime.model.Channel;
import com.nsu.runtime.model.node.RuntimeNode;
import java.util.Map;
import java.util.function.Function;

public final class MapNode<I, O> extends RuntimeNode<I, O> {

    private final Function<I, O> fn;

    public MapNode(
            String id,
            Channel<I> in,
            Channel<O> out,
            Function<I, O> fn
    ) {
        super(id, Map.of("in", in), Map.of("out", out));
        this.fn = fn;
    }

    @Override
    public void run() {
        try {
            Channel<I> in = inputs.get("in");
            Channel<O> out = outputs.get("out");

            while (true) {
                I v = in.take();
                if (v == null) {
                    out.end();
                    return;
                }
                out.put(fn.apply(v));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


