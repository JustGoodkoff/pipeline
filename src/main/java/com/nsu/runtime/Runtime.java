package com.nsu.runtime;

import java.util.List;

public class Runtime {

    private final List<Thread> threads;

    public Runtime(List<Thread> threads) {
        this.threads = threads;
    }

    public void start() {
        threads.forEach(Thread::start);
    }

    public void join() throws InterruptedException {
        for (Thread t : threads) {
            t.join();
        }
    }
}
