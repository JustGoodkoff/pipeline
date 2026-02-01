package com.nsu.runtime.model;

import com.nsu.runtime.End;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class Channel<T> {
    private final Class<T> type;
    private final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();

    public Channel(Class<T> type) {
        this.type = type;
    }

    public Class<T> type() {
        return type;
    }

    public void put(T value) throws InterruptedException {
        queue.put(value);
    }

    public void end() throws InterruptedException {
        queue.put(End.INSTANCE);
    }

    @SuppressWarnings("unchecked")
    public T take() throws InterruptedException {
        Object v = queue.take();
        if (v == End.INSTANCE) {
            queue.put(End.INSTANCE); // пробрасываем дальше
            return null;
        }
        return (T) v;
    }
}
