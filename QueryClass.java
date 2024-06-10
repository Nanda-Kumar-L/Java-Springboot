package com.javaspringml.marklogicjavaconnection.controller;

import org.springframework.stereotype.Component;

@Component
public class QueryClass {
    private String query;

    // Getters and Setters
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
