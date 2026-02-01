package com.nsu.runtime.model.node;

import com.nsu.runtime.model.Channel;
import java.util.Iterator;
import java.util.Map;

public final class SourceNode<T> extends RuntimeNode<Void, T> {

    private final Iterator<T> data;

    public SourceNode(
            String id,
            Channel<T> out,
            Iterator<T> data
    ) {
        super(id, Map.of(), Map.of("out", out));
        this.data = data;
    }

    @Override
    public void run() {
        try {
            Channel<T> out = outputs.get("out");

            while (data.hasNext()) {
                out.put(data.next());
            }
            out.end();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

