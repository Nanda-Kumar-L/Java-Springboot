package com.marklogicquery.run.xquery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class SequenceGenerator4 {

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
        return mlc.executeXQuery("data(doc(\"/itoquiz/SequenceGenerator\")/tXML/SequenceGenerator/SequenceGenerator4)");
    }

    public void setSequence(long id) {
        mlc.executeXQuery("xdmp:node-replace(doc(\"/itoquiz/SequenceGenerator\")/tXML/SequenceGenerator/SequenceGenerator4, element SequenceGenerator4{"+id+"})");
    }
}