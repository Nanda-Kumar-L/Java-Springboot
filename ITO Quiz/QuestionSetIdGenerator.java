package com.marklogicquery.run.xquery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class QuestionSetIdGenerator {

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
        mlc.executeXQuery("if(not(exists(doc(\"/itoquiz/SequenceGenerator\")))) then\n" +
                "xdmp:document-insert(\"/itoquiz/SequenceGenerator\", \n" +
                "  element tXML{\n" +
                "    element SequenceGenerator{\n" +
                "        element QuestionSetIdGenerator{\"0\"},\n" +
                "        element QuestionIdGenerator{\"0\"},\n" +
                "        element CandidateIdGenerator{\"0\"},\n" +
                "        element AnswerIdGenerator{\"0\"}\n" +
                "      }\n" +
                "    }, \n" +
                "  <options xmlns=\"xdmp:document-insert\">\n" +
                "  <collections>\n" +
                "  <collection>SequenceGenerator</collection>  \n" +
                "  </collections>\n" +
                "  </options>\n" +
                ")\n" +
                "else\n" +
                "(\n" +
                ")");
        return mlc.executeXQuery("data(doc(\"/itoquiz/SequenceGenerator\")/tXML/SequenceGenerator/QuestionSetIdGenerator)");
    }

    public void updateIdInDatabase(long id) {
        mlc.executeXQuery("xdmp:node-replace(doc(\"/itoquiz/SequenceGenerator\")/tXML/SequenceGenerator/QuestionSetIdGenerator, element QuestionSetIdGenerator{"+id+"})");
    }
}