package com.marklogicquery.run.xquery.model;


import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class Question {

    private String question_id;
    private String question;
    private List<String> options;
    private int answer;

}
