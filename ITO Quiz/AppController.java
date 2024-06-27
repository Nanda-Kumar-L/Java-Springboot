package com.marklogicquery.run.xquery.controller;

import com.marklogicquery.run.xquery.model.Answer;
import com.marklogicquery.run.xquery.model.Candidate;
import com.marklogicquery.run.xquery.model.Question;
import com.marklogicquery.run.xquery.repository.AdminRepository;
import com.marklogicquery.run.xquery.repository.CandidateRepository;
import com.marklogicquery.run.xquery.repository.PathRangeIndexRepository;
import com.marklogicquery.run.xquery.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    SequenceGenerator.QuestionSetIdGenerator questionSetIdGenerator;

    @Autowired
    SequenceGenerator.QuestionIdGenerator questionIdGenerator;

    @Autowired
    SequenceGenerator.CandidateIdGenerator candidateIdGenerator;

    @Autowired
    SequenceGenerator.AnswerIdGenerator answerIdGenerator;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    PathRangeIndexRepository pathRangeIndexRepository;

    @Autowired
    CandidateRepository candidateRepository;

    @PostMapping("/createQuestions")
    public String create_questions(@RequestBody List<Question> question)
    {
        pathRangeIndexRepository.createPathRangeIndexes();
        questionSetIdGenerator.setId(Long.parseLong(questionSetIdGenerator.fetchIdFromDatabase().trim()));
        long questionSetId = questionSetIdGenerator.nextId();
        adminRepository.CreateQuestionSet(questionSetId);
        StringBuilder sb = new StringBuilder();
        for(Question q:question) {
            questionIdGenerator.setId(Long.parseLong(questionIdGenerator.fetchIdFromDatabase().trim()));
            long questionId = questionIdGenerator.nextId();
            q.setQuestion_id(String.valueOf(questionId));
            sb.append(adminRepository.createQuestion(q, questionSetId));
            questionIdGenerator.updateIdInDatabase(questionId);
        }
        questionSetIdGenerator.updateIdInDatabase(questionSetId);
        return sb.toString();
    }

    @GetMapping(value="/getQuestions", produces = "application/json")
    public String get_questions()
    {
        return adminRepository.getAllQuestions();
    }

    @GetMapping(value="/getQuestionSetById",produces="application/json")
    public String get_question_set_by_id(@RequestParam String questionSetId)
    {
        return adminRepository.getQuestionSetById(questionSetId);
    }

    @GetMapping("/getQuestionById")
    public String get_question_by_id(@RequestParam String questionSetId,@RequestParam String questionId)
    {
        return adminRepository.getQuestionById(questionSetId,questionId);
    }

    @PutMapping("/updateQuestion")
    public String update_question_by_id(@RequestParam String questionSetId,@RequestParam String questionId,@RequestBody List<Question> question)
    {
        StringBuilder sb = new StringBuilder();
        String[] s= questionId.split(",");
        int i=0;
        for(Question q:question)
        {
            q.setQuestion_id(s[i++]);
            sb.append(adminRepository.updateQuestionById(questionSetId,s[i-1],q));
        }
        return sb.toString();
    }


    @DeleteMapping("/deleteQuestion")
    public String delete_question_by_id(@RequestParam String questionSetId,@RequestParam String questionId)
    {
        StringBuilder sb = new StringBuilder();
        String[] questionIds= questionId.split(",");
        int i=0;
        for(String id:questionIds)
        {
            sb.append(adminRepository.deleteQuestionById(questionSetId,id));
        }
        return sb.toString();
    }

    @GetMapping("/Evaluate")
    public String evaluate(@RequestParam String candidateId,@RequestParam String answerId)
    {
        return adminRepository.evaluate(candidateId,answerId);
    }

    @PostMapping("/assessment/getCandidateId")
    public String get_candidate_id(@RequestBody Candidate candidate)
    {
        candidateIdGenerator.setId(Long.parseLong(candidateIdGenerator.fetchIdFromDatabase().trim()));
        long id = candidateIdGenerator.nextId();
        candidate.setCandidate_id(String.valueOf(id));
        candidateIdGenerator.updateIdInDatabase(id);
        return candidateRepository.getCandidateId(candidate);
    }

    @GetMapping("/Assessment/getQuestionSet")
    public String get_question_set(@RequestParam String candidateId,@RequestParam(required = false) String questionSetId)
    {
        return candidateRepository.getQuestionSet(candidateId,questionSetId);
    }

    @PostMapping("/Assessment/submitAnswerSheet")
    public String submit_answer_sheet(@RequestParam String candidateId, @RequestBody Answer answer)
    {
        answerIdGenerator.setId(Long.parseLong(answerIdGenerator.fetchIdFromDatabase().trim()));
        long Id = answerIdGenerator.nextId();
        String id = String.valueOf(Id);
        answer.setId(id);
        answer.setCandidate_id(candidateId);
        answerIdGenerator.updateIdInDatabase(Id);
        return candidateRepository.submitAnswerSheet(candidateId,answer);
    }


}
