package com.nsu.runtime;

import com.nsu.runtime.model.node.RuntimeNode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RuntimeGraph {

    private final Map<String, RuntimeNode<?, ?>> nodes = new LinkedHashMap<>();
    private final List<Thread> threads = new ArrayList<>();

    public void addNode(RuntimeNode<?, ?> node) {
        nodes.put(node.id, node);
        threads.add(new Thread(node, node.id));
    }

    public void start() {
        threads.forEach(Thread::start);
    }

    public void join() {
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    public Map<String, RuntimeNode<?, ?>> getNodes() {
        return nodes;
    }
}
