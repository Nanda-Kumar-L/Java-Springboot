package com.marklogicquery.run.xquery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class QuestionIdGenerator {

    @Autowired
    MarkLogicConnection mlc;

    private final AtomicLong sequence = new AtomicLong();

    public long nextId() {
        return sequence.incrementAndGet();
    }

    public synchronized void setId(long initialValue) {
        sequence.set(initialValue);
    }

    public String fetchIdFromDatabase() {
        return mlc.executeXQuery("data(doc(\"/itoquiz/SequenceGenerator\")/tXML/SequenceGenerator/QuestionIdGenerator)");
    }

    public void updateIdInDatabase(long id) {
        mlc.executeXQuery("xdmp:node-replace(doc(\"/itoquiz/SequenceGenerator\")/tXML/SequenceGenerator/QuestionIdGenerator, element QuestionIdGenerator{"+id+"})");
    }
}