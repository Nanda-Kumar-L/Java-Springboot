package com.marklogicquery.run.xquery.controller;

import com.marklogicquery.run.xquery.model.Answer;
import com.marklogicquery.run.xquery.model.Candidate;
import com.marklogicquery.run.xquery.model.Question;
import com.marklogicquery.run.xquery.repository.AdminRepository;
import com.marklogicquery.run.xquery.repository.CandidateRepository;
import com.marklogicquery.run.xquery.repository.PathRangeIndex;
import com.marklogicquery.run.xquery.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private SequenceGenerator sg1;

    @Autowired
    private SequenceGenerator2 sg2;

    @Autowired
    private SequenceGenerator3 sg3;

    @Autowired
    private SequenceGenerator4 sg4;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PathRangeIndex pathRangeIndex;

    @Autowired
    private CandidateRepository candidateRepository;

    @PostMapping("/createQuestions")
    public String create_questions(@RequestBody List<Question> question)
    {
        pathRangeIndex.createPathRangeIndexes();
        sg1.reset(Long.parseLong(sg1.getSequence().trim()));
        long id1 = sg1.nextId();
        adminRepository.CreateQuestionSet(id1);
        StringBuilder sb = new StringBuilder();
        for(Question q:question) {
            sg2.reset(Long.parseLong(sg2.getSequence().trim()));
            long id2 = sg2.nextId();
            q.setQuestion_id(String.valueOf(id2));
            sb.append(adminRepository.createQuestion(q, id1));
            sg2.setSequence(id2);
        }
        sg1.setSequence(id1);
        return sb.toString();
    }

    @GetMapping("/getQuestions")
    public String get_questions()
    {
        return adminRepository.getAllQuestions();
    }

    @GetMapping("/getQuestionSetById/{id}")
    public String get_question_set_by_id(@PathVariable String id)
    {
        return adminRepository.getQuestionSetById(id);
    }

    @GetMapping("/getQuestionById/{questionSetId}/{questionId}")
    public String get_question_by_id(@PathVariable String questionSetId,@PathVariable String questionId)
    {
        return adminRepository.getQuestionById(questionSetId,questionId);
    }

    @PutMapping("/updateQuestion/{questionSetId}/{questionId}")
    public String update_question_by_id(@PathVariable String questionSetId,@PathVariable String questionId,@RequestBody List<Question> question)
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


    @DeleteMapping("/deleteQuestion/{questionSetId}/{questionId}")
    public String delete_question_by_id(@PathVariable String questionSetId,@PathVariable String questionId)
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

    @GetMapping("/Evaluate/{candidateId}")
    public String evaluate(@PathVariable String candidateId)
    {
        return adminRepository.evaluate(candidateId);
    }

    @PostMapping("/Assessment/getCandidateId")
    public String get_candidate_id(@RequestBody Candidate candidate)
    {
        sg3.reset(Long.parseLong(sg3.getSequence().trim()));
        long id = sg3.nextId();
        candidate.setCandidate_id(String.valueOf(id));
        sg3.setSequence(id);
        return candidateRepository.getCandidateId(candidate);
    }

    @GetMapping("/Assessment/getQuestionSet/{candidateId}")
    public String get_question_set(@PathVariable String candidateId)
    {
        return candidateRepository.getQuestionSet(candidateId);
    }

    @PostMapping("/Assessment/submitAnswerSheet/{candidateId}")
    public String submit_answer_sheet(@PathVariable String candidateId, @RequestBody Answer answer)
    {
        sg4.reset(Long.parseLong(sg4.getSequence().trim()));
        long Id = sg4.nextId();
        String id = String.valueOf(Id);
        answer.setId(id);
        answer.setCandidate_id(candidateId);
        sg4.setSequence(Id);
        return candidateRepository.submitAnswerSheet(candidateId,answer);
    }


}
