package com.marklogicquery.run.xquery.model;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class Answer{
    private String id;
    private String candidate_id;
    private String question_id;
    private int answer;
}
