package com.marklogicquery.run.xquery.controller;

import com.marklogicquery.run.xquery.model.Question;
import com.marklogicquery.run.xquery.service.PathRangeIndex;
import com.marklogicquery.run.xquery.service.QuestionService;
import com.marklogicquery.run.xquery.service.SequenceGenerator;
import com.marklogicquery.run.xquery.service.SequenceGenerator2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private SequenceGenerator sg;

    @Autowired
    private SequenceGenerator2 sg2;

    @Autowired
    private QuestionService qs;

    @Autowired
    private PathRangeIndex pri;


    @PostMapping("/createPathRangeIndexes")
    public String create_pathRangeIndexes()
    {
        return pri.createPathRangeIndexes();
    }

    @PostMapping("/createQuestions")
    public String create_questions(@RequestBody List<Question> question)
    {
        long id = sg.nextId();
        qs.CreateQuestionSet(id);
        StringBuilder sb = new StringBuilder();
        for(Question q:question) {
            q.setQuestion_id(String.valueOf(sg2.nextId()));
            sb.append(qs.createQuestion(q,id));
        }
        sg2.reset(0);
        return sb.toString();
    }

    @GetMapping("/getQuestions")
    public String get_questions()
    {
        return qs.getAllQuestions();
    }

    @GetMapping("/getQuestionSetById/{id}")
    public String get_question_set_by_id(@PathVariable String id)
    {
        return qs.getQuestionSetById(id);
    }

    @GetMapping("/getQuestionById/{questionSetId}/{questionId}")
    public String get_question_by_id(@PathVariable String questionSetId,@PathVariable String questionId)
    {
        return qs.getQuestionById(questionSetId,questionId);
    }

    @PutMapping("/updateQuestion/{questionSetId}/{questionId}")
    public String update_question_by_id(@PathVariable String questionSetId,@PathVariable String questionId,@RequestBody Question q)
    {
        q.setQuestion_id(questionId);
        return qs.updateQuestionById(questionSetId,questionId,q);
    }

    @PutMapping("/updateQuestionSet/{questionSetId}")
    public String update_question_set_by_id(@PathVariable String questionSetId,@RequestBody List<Question> question)
    {
        StringBuilder sb = new StringBuilder();
        for(Question q:question) {
            q.setQuestion_id(String.valueOf(sg2.nextId()));
            sb.append(qs.updateQuestionSetById(questionSetId,q));
        }
        sg2.reset(0);
        return sb.toString();
    }

}
