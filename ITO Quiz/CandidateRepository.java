package com.marklogicquery.run.xquery.repository;

import com.marklogicquery.run.xquery.model.Answer;
import com.marklogicquery.run.xquery.model.Candidate;
import com.marklogicquery.run.xquery.service.MarkLogicConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateRepository {

    @Autowired
    MarkLogicConnection mlc;

    private String createAnswerElements(List<Integer> answers) {
        StringBuilder sb = new StringBuilder();
        for (Integer s : answers) {
            sb.append("element answer{").append(s).append("},");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private String createQuestionIdElements(List<String> questionIds) {
        StringBuilder sb = new StringBuilder();
        for (String s : questionIds) {
            sb.append("element questionId{\"").append(s).append("\"},");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public String getCandidateId(Candidate candidate) {
        try{
        if (candidate.getName() == null || candidate.getEmailId() == null)
            throw new Exception("Please enter all the fields");
        return mlc.executeXQuery("if(not(exists(cts:search(/tXML/Candidate,cts:and-query((cts:collection-query(\"Candidate\"),cts:path-range-query(\"/tXML/Candidate/emailId\", \"=\", \"" + candidate.getEmailId() + "\"))))))) then\n" +
                "(xdmp:document-insert(\"/itoquiz/Candidate/\"||\"" + candidate.getCandidate_id() + "\", \n" +
                "  element tXML{\n" +
                "    element Candidate{\n" +
                "        element candidate_id{\"" + candidate.getCandidate_id() + "\"},\n" +
                "        element name{\"" + candidate.getName() + "\"},\n" +
                "        element emailId{\"" + candidate.getEmailId() + "\"},\n" +
                "        element isStarted{false()},\n" +
                "        element isSubmit{false()}\n" +
                "        }\n" +
                "    }, \n" +
                "  <options xmlns=\"xdmp:document-insert\">\n" +
                "  <collections>\n" +
                "  <collection>Candidate</collection>  \n" +
                "  </collections>\n" +
                "  </options>\n" +
                "),\"Candidate Id = <" + candidate.getCandidate_id() + ">\")\n" +
                "else\n" +
                "\"Email Id exists, please enter different email id\"");
        }
        catch(Exception e){
            return "Error: "+e.getMessage()+"\n";
        }
    }

    public String getQuestionSet(String candidateId) {
        return mlc.executeXQuery("declare function local:getRandomValues($count)\n" +
                "{\n" +
                "  let $numbers := fn:distinct-values((1 to 10) ! (xdmp:random($count - 1)+1))\n" +
                "  return\n" +
                "  if(count($numbers) = 10) then\n" +
                "  $numbers\n" +
                "  else\n" +
                "  local:getRandomValues($count)\n" +
                "};\n" +
                "let $candidate := cts:search(/tXML/Candidate,cts:and-query((cts:collection-query(\"Candidate\"),cts:path-range-query(\"/tXML/Candidate/candidate_id\", \"=\", \""+candidateId+"\"))))\n" +
                "return\n" +
                "if(exists($candidate)) then\n" +
                "(\n" +
                "  if($candidate/isStarted eq false()) then\n" +
                "  (\n" +
                "    let $count := count(cts:search(/tXML/Questions/Question, (cts:collection-query(\"Questions\"))))\n" +
                "    return\n" +
                "    if($count<10) then\n" +
                "    \"There isn't 10 questions in the database\"\n" +
                "    else\n" +
                "    (\n" +
                "    let $_ := xdmp:node-replace($candidate/isStarted, element isStarted{true()})\n" +
                "    let $questionSet := cts:search(/tXML/Questions/Question, (cts:collection-query(\"Questions\")))[local:getRandomValues($count)]\n" +
                "    let $json := map:map()\n" +
                "    let $Questions:=\n" +
                "      for $question in $questionSet\n" +
                "      let $Question := map:map()\n" +
                "      let $_:= map:put($Question, \"QuestionId\", data($question/questionId))\n" +
                "      let $_:= map:put($Question, \"Question\", data($question/question))\n" +
                "      let $_:= map:put($Question, \"Options\", data($question/options/option))\n" +
                "      return xdmp:to-json($Question)\n" +
                "    let $questionArray := json:to-array($Questions)\n" +
                "    let $_ := map:put($json, \"questions\", $questionArray)\n" +
                "    let $_ := map:put($json, \"id\", data($questionSet/Id))\n" +
                "    return xdmp:to-json($json)\n" +
                "    )\n" +
                "  )\n" +
                "  else \"Exam Assessment Running\"\n" +
                ")\n" +
                "else \"Candidate Id doesn’t exist\"");
    }

    public String submitAnswerSheet(String candidateId, Answer answer) {
        try {
            if (answer.getQuestion_ids().size() != 10)
                throw new Exception("Please enter 10 question ids");
            if (answer.getAnswers().size() != 10)
                throw new Exception("Please enter 10 answers");
            return mlc.executeXQuery("let $candidate := cts:search(/tXML/Candidate,cts:and-query((cts:collection-query(\"Candidate\"),cts:path-range-query(\"/tXML/Candidate/candidate_id\", \"=\", \"" + candidateId + "\"))))\n" +
                    "return\n" +
                    "if(exists($candidate)) then\n" +
                    "(\n" +
                    "  if($candidate/isStarted eq true()) then\n" +
                    "  (" +
                    "  if($candidate/isSubmit eq false()) then\n" +
                    "  (\n" +
                    "    xdmp:document-insert(\"/itoquiz/Answer/\"||\"" + answer.getId() + "\", \n" +
                    "    element tXML{\n" +
                    "      element Answer{\n" +
                    "          element Id{\"" + answer.getId() + "\"},\n" +
                    "          element candidateId{$candidate/candidate_id/string()},\n" +
                    "          element questionIds{" + createQuestionIdElements(answer.getQuestion_ids()) + "},\n" +
                    "          element answers{" + createAnswerElements(answer.getAnswers()) + "}\n" +
                    "        }\n" +
                    "      }, \n" +
                    "    <options xmlns=\"xdmp:document-insert\">\n" +
                    "    <collections>\n" +
                    "    <collection>Answers</collection>  \n" +
                    "    </collections>\n" +
                    "    </options>\n" +
                    "    )\n" +
                    "    ,\n" +
                    "    xdmp:node-replace($candidate/isSubmit, element isSubmit{true()})\n" +
                    "    ,\n" +
                    "    let $score :=\n" +
                    "      let $count := (0)\n" +
                    "      let $questions := element questionIds{" + createQuestionIdElements(answer.getQuestion_ids()) + "}\n" +
                    "      let $answers := element answers{" + createAnswerElements(answer.getAnswers()) + "}\n" +
                    "      for $questionId at $x in $questions/questionId\n" +
                    "      let $storedQuestionInDatabase := cts:search(/tXML/Questions/Question,cts:and-query((cts:collection-query(\"Questions\"),cts:path-range-query(\"/tXML/Questions/Question/questionId\", \"=\", $questionId))))\n" +
                    "      let $_ := if($answers/answer[$x] eq $storedQuestionInDatabase/answer) then xdmp:set($count, $count + 1) else ()\n" +
                    "      return $count\n" +
                    "    return if(fn:max($score) > 6) then \"Selected for the next Round\" else \"Sorry you are not selected. Better luck next time\"\n" +
                    "  )\n" +
                    "  else \"Answer already submitted\"\n" +
                    "  )\n" +
                    "  else \"Exam is not started yet\"" +
                    ")\n" +
                    "else \"Candidate Id doesn’t exist\"");
            } catch (Exception e) {
            return "Error: " + e.getMessage() + "\n";
        }
    }

}
