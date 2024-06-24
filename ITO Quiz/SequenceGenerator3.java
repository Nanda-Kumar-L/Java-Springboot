package com.marklogicquery.run.xquery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class SequenceGenerator3 {

    @Autowired
    MarkLogicConnection mlc;

    private final AtomicLong sequence = new AtomicLong();

    public long nextId() {
        return sequence.incrementAndGet();
    }

    public synchronized void reset(long initialValue) {
        sequence.set(initialValue);
    }

    public String getSequence() {
        return mlc.executeXQuery("data(doc(\"/itoquiz/SequenceGenerator\")/tXML/SequenceGenerator/SequenceGenerator3)");
    }

    public void setSequence(long id) {
        mlc.executeXQuery("xdmp:node-replace(doc(\"/itoquiz/SequenceGenerator\")/tXML/SequenceGenerator/SequenceGenerator3, element SequenceGenerator3{"+id+"})");
    }
}