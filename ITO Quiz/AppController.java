package com.marklogicquery.run.xquery.controller;

import com.marklogicquery.run.xquery.model.Question;
import com.marklogicquery.run.xquery.service.QuestionService;
import com.marklogicquery.run.xquery.service.SequenceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public String create_questions(@RequestBody List<Question> question)
    {
        mlc.executeXQuery("");
        StringBuilder sb = new StringBuilder();
        for(Question q:question) {
            q.setQuestion_id(String.valueOf(sg.nextId()));
            sb.append(qs.createQuestion(q));
        }
        return sb.toString();
    }

    @GetMapping("/getQuestions")
    public String get_questions()
    {
        return qs.getAllQuestions();
    }

    @GetMapping("/getQuestionById/{id}")
    public String get_question_by_id(@PathVariable String id)
    {
        return qs.getQuestionById(id);
    }

    @PutMapping("/updateQuestion/{id}")
    public String update_question_by_id(@PathVariable String id,@RequestBody Question q)
    {
        q.setQuestion_id(id);
        return qs.updateQuestionById(id,q);
    }


}
