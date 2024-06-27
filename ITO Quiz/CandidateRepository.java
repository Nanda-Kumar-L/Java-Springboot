package com.marklogicquery.run.xquery.repository;

import com.marklogicquery.run.xquery.model.Answer;
import com.marklogicquery.run.xquery.model.Candidate;
import com.marklogicquery.run.xquery.model.QuestionIdAndAnswer;
import com.marklogicquery.run.xquery.service.MarkLogicConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CandidateRepository {

    @Autowired
    MarkLogicConnection mlc;

    private String getObjectElements(List<QuestionIdAndAnswer> objects) throws Exception {

        StringBuilder sb = new StringBuilder();
        for (QuestionIdAndAnswer questionIdAndAnswer : objects) {
            if (questionIdAndAnswer.getAnswer() ==null)
                throw new Exception("Please enter 10 answers");
            if (questionIdAndAnswer.getQuestion_id() ==null)
                throw new Exception("Please enter 10 question Ids");
            sb.append("element Object{");
            sb.append("element answer{").append(questionIdAndAnswer.getAnswer()).append("},");
            sb.append("element questionId{\"").append(questionIdAndAnswer.getQuestion_id()).append("\"}},");
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

    public String getQuestionSet(String candidateId, String questionSetId) {
        return mlc.executeXQuery("declare function local:getRandomIndexValues($CountOfExistingQuestions,$max)\n" +
                "{\n" +
                "  let $numbers := fn:distinct-values((1 to $max) ! (xdmp:random($CountOfExistingQuestions - 1)+1))\n" +
                "  return\n" +
                "    if(count($numbers) = $max) then\n" +
                "      $numbers\n" +
                "    else\n" +
                "      local:getRandomIndexValues($CountOfExistingQuestions,$max)\n" +
                "};\n" +
                "declare function local:getJson($questionSet)\n" +
                "{\n" +
                "  let $json := map:map()\n" +
                "  let $Questions:=\n" +
                "    for $question in $questionSet\n" +
                "    let $Question := map:map()\n" +
                "    let $_:= map:put($Question, \"QuestionId\", data($question/questionId))\n" +
                "    let $_:= map:put($Question, \"Question\", data($question/question))\n" +
                "    let $_:= map:put($Question, \"Options\", data($question/options/option))\n" +
                "    return xdmp:to-json($Question)\n" +
                "  let $questionArray := json:to-array($Questions)\n" +
                "  let $_ := map:put($json, \"questions\", $questionArray)\n" +
                "  let $_ := map:put($json, \"id\", data($questionSet/Id))\n" +
                "  return xdmp:to-json($json)\n" +
                "};\n" +
                "\n" +
                "let $candidate := cts:search(/tXML/Candidate,cts:and-query((cts:collection-query(\"Candidate\"),cts:path-range-query(\"/tXML/Candidate/candidate_id\", \"=\", \""+candidateId+"\"))))\n" +
                "return\n" +
                "if(exists($candidate)) then\n" +
                "(\n" +
                "  if($candidate/isSubmit eq false()) then\n" +
                "  (\n" +
                "  if($candidate/isStarted eq false()) then\n" +
                "  (\n" +
                "    let $CountOfExistingQuestions := count(cts:search(/tXML/Questions/Question, (cts:collection-query(\"Questions\"))))\n" +
                "    let $max := 10\n" +
                "    return\n" +
                "    if($CountOfExistingQuestions < $max) then\n" +
                "    \"There isn't \"||$max||\" questions in the database\"\n" +
                "    else\n" +
                "    (\n" +
                "      let $questionSetId := \""+questionSetId+"\"\n" +
                "      let $questionSet := cts:search(/tXML/Questions, cts:and-query((cts:collection-query(\"Questions\"),cts:path-range-query(\"/tXML/Questions/Id\", \"=\", $questionSetId))))\n" +
                "      return\n" +
                "      if($questionSetId eq \"null\") then \n" +
                "      (\n" +
                "        let $_ := xdmp:node-replace($candidate/isStarted, element isStarted{true()})\n" +
                "        let $questions := cts:search(/tXML/Questions/Question, (cts:collection-query(\"Questions\")))[local:getRandomIndexValues($CountOfExistingQuestions,$max)]\n" +
                "        return local:getJson($questions)\n" +
                "      )\n" +
                "      else if(exists($questionSet)) then\n" +
                "      (\n" +
                "        if(count($questionSet/Question) eq $max) then\n" +
                "        (\n" +
                "          let $_ := xdmp:node-replace($candidate/isStarted, element isStarted{true()})\n" +
                "          let $questions := $questionSet/Question\n" +
                "          return local:getJson($questions)\n" +
                "        )\n" +
                "        else \"The question set doesn't have \"||$max||\" questions\"\n" +
                "      )\n" +
                "      else \"Invalid QuestionSet Id\"\n" +
                "    )\n" +
                "  )\n" +
                "  else \"Exam Assessment Running\"\n" +
                "  )\n" +
                "  else \"Exam already submitted\"\n" +
                ")\n" +
                "else \"Candidate Id doesn’t exist\"");
    }

    public String submitAnswerSheet(String candidateId, Answer answer) {
        try {
            if (answer.getObjects().size() != 10)
                throw new Exception("Please enter 10 question ids and answers");
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
                    "          element questionIdsAndAnswers{" + getObjectElements(answer.getObjects()) + "}\n" +
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
                    "      let $questionIdsAndAnswers := element questionIdsAndAnswers{" + getObjectElements(answer.getObjects()) + "}\n" +
                    "      for $questionIdAndAnswerObject in $questionIdsAndAnswers/Object\n" +
                    "      let $storedQuestionInDatabase := cts:search(/tXML/Questions/Question,cts:and-query((cts:collection-query(\"Questions\"),cts:path-range-query(\"/tXML/Questions/Question/questionId\", \"=\", $questionIdAndAnswerObject/questionId))))\n" +
                    "      let $_ := if($questionIdAndAnswerObject/answer eq $storedQuestionInDatabase/answer) then xdmp:set($count, $count + 1) else ()\n" +
                    "      return $count\n" +
                    "    return if(fn:max($score) > 6) then \"Selected for the next Round\" else \"Sorry you are not selected. Better luck next time\"|| \"      AnswerID=/\"||\""+ answer.getId() +"\"\n" +
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
