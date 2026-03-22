package com.fintech.gateway;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A custom ThreadFactory that provides descriptive names for threads in the pool.
 * This aids in debugging and log readability.
 */
public class NamedThreadFactory implements ThreadFactory {
    private final String baseName;
    private final AtomicInteger threadId = new AtomicInteger(1);

    public NamedThreadFactory(String baseName) {
        this.baseName = baseName;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(baseName + "-" + threadId.getAndIncrement());
        return thread;
    }
}
