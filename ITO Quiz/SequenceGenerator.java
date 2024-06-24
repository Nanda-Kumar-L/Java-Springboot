package com.marklogicquery.run.xquery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class SequenceGenerator {

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
        mlc.executeXQuery("if(not(exists(doc(\"/itoquiz/SequenceGenerator\")))) then\n" +
                "xdmp:document-insert(\"/itoquiz/SequenceGenerator\", \n" +
                "  element tXML{\n" +
                "    element SequenceGenerator{\n" +
                "        element SequenceGenerator1{\"0\"},\n" +
                "        element SequenceGenerator2{\"0\"},\n" +
                "        element SequenceGenerator3{\"0\"},\n" +
                "        element SequenceGenerator4{\"0\"}\n" +
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
        return mlc.executeXQuery("data(doc(\"/itoquiz/SequenceGenerator\")/tXML/SequenceGenerator/SequenceGenerator1)");
    }

    public void setSequence(long id) {
        mlc.executeXQuery("xdmp:node-replace(doc(\"/itoquiz/SequenceGenerator\")/tXML/SequenceGenerator/SequenceGenerator1, element SequenceGenerator1{"+id+"})");
    }
}