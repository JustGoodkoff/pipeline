package com.nsu.runtime.model.node;

import com.nsu.runtime.model.Channel;
import java.util.Map;

public final class SinkNode<T> extends RuntimeNode<T, Void> {

    public SinkNode(String id, Channel<T> in) {
        super(id, Map.of("in", in), Map.of());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        try {
            Channel<T> in = inputs.get("in");

            while (true) {
                T v = in.take();
                if (v == null) {
                    System.out.println("[" + id + "] завершён");
                    return;
                }
                System.out.println("RESULT: " + v);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

