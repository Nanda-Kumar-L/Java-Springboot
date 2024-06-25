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
    private QuestionSetIdGenerator questionSetIdGenerator;

    @Autowired
    private QuestionIdGenerator questionIdGenerator;

    @Autowired
    private CandidateIdGenerator candidateIdGenerator;

    @Autowired
    private AnswerIdGenerator answerIdGenerator;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PathRangeIndexRepository pathRangeIndexRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @PostMapping("/createQuestions")
    public String create_questions(@RequestBody List<Question> question)
    {
        pathRangeIndexRepository.createPathRangeIndexes();
        questionSetIdGenerator.setId(Long.parseLong(questionSetIdGenerator.fetchIdFromDatabase().trim()));
        long id1 = questionSetIdGenerator.nextId();
        adminRepository.CreateQuestionSet(id1);
        StringBuilder sb = new StringBuilder();
        for(Question q:question) {
            questionIdGenerator.setId(Long.parseLong(questionIdGenerator.fetchIdFromDatabase().trim()));
            long id2 = questionIdGenerator.nextId();
            q.setQuestion_id(String.valueOf(id2));
            sb.append(adminRepository.createQuestion(q, id1));
            questionIdGenerator.updateIdInDatabase(id2);
        }
        questionSetIdGenerator.updateIdInDatabase(id1);
        return sb.toString();
    }

    @GetMapping("/getQuestions")
    public String get_questions()
    {
        return adminRepository.getAllQuestions();
    }

    @GetMapping("/getQuestionSetById")
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
    public String evaluate(@RequestParam String candidateId)
    {
        return adminRepository.evaluate(candidateId);
    }

    @PostMapping("/Assessment/getCandidateId")
    public String get_candidate_id(@RequestBody Candidate candidate)
    {
        candidateIdGenerator.setId(Long.parseLong(candidateIdGenerator.fetchIdFromDatabase().trim()));
        long id = candidateIdGenerator.nextId();
        candidate.setCandidate_id(String.valueOf(id));
        candidateIdGenerator.updateIdInDatabase(id);
        return candidateRepository.getCandidateId(candidate);
    }

    @GetMapping("/Assessment/getQuestionSet")
    public String get_question_set(@RequestParam String candidateId)
    {
        return candidateRepository.getQuestionSet(candidateId);
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
