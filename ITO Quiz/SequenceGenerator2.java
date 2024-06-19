package com.marklogicquery.run.xquery.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class SequenceGenerator2 {
    private final AtomicLong sequence = new AtomicLong();

    public long nextId() {
        return sequence.incrementAndGet();
    }

    public synchronized void reset(long initialValue) {
        sequence.set(initialValue);
    }
}