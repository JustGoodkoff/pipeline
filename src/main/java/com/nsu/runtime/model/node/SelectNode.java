package com.nsu.runtime.model.node;

import com.nsu.runtime.model.Channel;
import java.util.Comparator;
import java.util.Map;

public final class SelectNode<T> extends RuntimeNode<T, T> {

    private final Comparator<T> comparator;

    public SelectNode(
            String id,
            Map<String, Channel<T>> inputs,
            Channel<T> out,
            Comparator<T> comparator
    ) {
        super(id, Map.copyOf(inputs), Map.of("out", out));
        this.comparator = comparator;
    }

    @Override
    public void run() {
        try {
            Channel<T> outChannel = outputs.get("out");

            while (true) {
                T best = null;

                for (Channel<T> p : inputs.values()) {
                    T v = p.take();

                    // сигнал окончания хотя бы на одном входе
                    if (v == null) {
                        outChannel.end();
                        return;
                    }

                    if (best == null || comparator.compare(v, best) > 0) {
                        best = v;
                    }
                }

                outChannel.put(best);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

