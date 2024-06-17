package com.marklogicquery.run.xquery.controller;

import com.marklogicquery.run.xquery.model.Question;
import com.marklogicquery.run.xquery.service.QuestionService;
import com.marklogicquery.run.xquery.service.SequenceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private MarkLogicConnection mlc;

    @Autowired
    private SequenceGenerator sg;
    @Autowired
    private QuestionService qs;


    @PostMapping("/createQuestions")
    public String createquestions(@RequestBody Question q)
    {
        return qs.createQuestion(q);
    }


}
