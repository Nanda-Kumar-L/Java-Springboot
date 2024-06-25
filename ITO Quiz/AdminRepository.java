package com.marklogicquery.run.xquery.repository;

import com.marklogicquery.run.xquery.model.Question;
import com.marklogicquery.run.xquery.service.MarkLogicConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
public class AdminRepository {
    @Autowired
    private MarkLogicConnection mlc;

    public String getOptionElements(List<String> options) {
        StringBuilder sb = new StringBuilder();
        for (String s : options) {
            sb.append("element option{\"").append(s).append("\"},");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public void CreateQuestionSet(long id) {
        mlc.executeXQuery("import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";\n" +
                "xdmp:document-insert(\"/itoquiz/Questions/\"||\""+id+"\", \n" +
                "  element tXML{\n" +
                "    element Questions{\n" +
                "        element Id{\""+id+"\"}\n" +
                "      }\n" +
                "    }, \n" +
                "  <options xmlns=\"xdmp:document-insert\">\n" +
                "  <collections>\n" +
                "  <collection>Questions</collection>  \n" +
                "  </collections>\n" +
                "  </options>\n" +
                ")\n"
        );
    }

    public String createQuestion(Question q, long id) {
        try{
            if(q.getOptions()==null)
                throw new Exception("Please enter all the fields");
            if(q.getOptions().size()!=4)
                throw new Exception("Minimum 4 options required");
            if(q.getAnswer() <= 0 || q.getAnswer()>4)
                throw new Exception("Please enter between 1 to 4 for answer field");
            if(q.getQuestion_id() == null || q.getQuestion() == null)
                throw new Exception("Please enter all the fields");
            return mlc.executeXQuery(
                    "xdmp:node-insert-child(cts:search(/tXML/Questions, cts:and-query((cts:collection-query(\"Questions\"),cts:path-range-query(\"/tXML/Questions/Id\", \"=\", \""+id+"\")))), \n" +
                            "element Question{  element questionId{\""+q.getQuestion_id()+"\"}, element question{\""+q.getQuestion()+"\"},element options{"+getOptionElements(q.getOptions())+"},element answer{"+q.getAnswer()+"}  })"+
                            ",\"Question <" + q.getQuestion_id() + "> generated successfully\""
            );
        }
        catch(Exception e){
            return "Failed to Generate Question <"+q.getQuestion_id()+">  =>  "+"Error: "+e.getMessage()+"\n";
        }

    }


    public String getAllQuestions() {
        try{
            return mlc.executeXQuery("""
                    let $a := for $i in cts:search(/tXML/Questions,cts:collection-query("Questions"))
                              let $json := map:map()
                              let $Questions:=
                                for $j in $i/Question
                                let $Question := map:map()
                                let $_:= map:put($Question, "QuestionId", data($j/questionId))
                                let $_:= map:put($Question, "Question", data($j/question))
                                let $_:= map:put($Question, "Answer", data($j/answer))
                                let $_:= map:put($Question, "Options", data($j/options/option))
                                return xdmp:to-json($Question)
                              let $questionArray := json:to-array($Questions)
                              let $_ := map:put($json, "questions", $questionArray)
                              let $_ := map:put($json, "id", data($i/Id))
                              return xdmp:to-json($json)
                    return json:to-array($a)""");
        }
        catch (Exception e){
            return e.getMessage();
        }
    }

    public String getQuestionSetById(String id) {
        return mlc.executeXQuery("let $questionSet := cts:search(/tXML/Questions, cts:and-query((cts:collection-query(\"Questions\"),cts:path-range-query(\"/tXML/Questions/Id\", \"=\", \""+id+"\"))))\n" +
                "let $json := map:map()\n" +
                "let $Questions:=\n" +
                "  for $question in $questionSet/Question\n" +
                "  let $Question := map:map()\n" +
                "  let $_:= map:put($Question, \"QuestionId\", data($question/questionId))\n" +
                "  let $_:= map:put($Question, \"Question\", data($question/question))\n" +
                "  let $_:= map:put($Question, \"Answer\", data($question/answer))\n" +
                "  let $_:= map:put($Question, \"Options\", data($question/options/option))\n" +
                "  return xdmp:to-json($Question)\n" +
                "let $questionArray := json:to-array($Questions)\n" +
                "let $_ := map:put($json, \"questions\", $questionArray)\n" +
                "let $_ := map:put($json, \"id\", data($questionSet/Id))\n" +
                "return xdmp:to-json($json)");
    }

    public String getQuestionById(String questionSetId, String questionId) {
        return mlc.executeXQuery("let $questionSet := cts:search(/tXML/Questions, cts:and-query((cts:collection-query(\"Questions\"),cts:path-range-query(\"/tXML/Questions/Id\", \"=\", \""+questionSetId+"\"))))\n" +
                "let $json := map:map()\n" +
                "let $Questions:=\n" +
                "  let $question := $questionSet/Question[questionId eq \""+questionId+"\"]\n" +
                "  let $Question := map:map()\n" +
                "  let $_:= map:put($Question, \"QuestionId\", data($question/questionId))\n" +
                "  let $_:= map:put($Question, \"Question\", data($question/question))\n" +
                "  let $_:= map:put($Question, \"Answer\", data($question/answer))\n" +
                "  let $_:= map:put($Question, \"Options\", data($question/options/option))\n" +
                "  return xdmp:to-json($Question)\n" +
                "let $questionArray := json:to-array($Questions)\n" +
                "let $_ := map:put($json, \"questions\", $questionArray)\n" +
                "let $_ := map:put($json, \"id\", data($questionSet/Id))\n" +
                "return xdmp:to-json($json)");
    }

    public String updateQuestionById(String questionSetId, String questionId, Question q) {
        try{
            if(q.getOptions()==null)
                throw new Exception("Please enter all the fields");
            if(q.getOptions().size()!=4)
                throw new Exception("Minimum 4 options required");
            if(q.getAnswer() <= 0 || q.getAnswer()>4)
                throw new Exception("Please enter between 1 to 4 for answer field");
            if(q.getQuestion() == null)
                throw new Exception("Please enter all the fields");
            if(Boolean.parseBoolean(mlc.executeXQuery("xs:string(not(exists(cts:search(/tXML/Questions, cts:and-query((cts:collection-query(\"Questions\"),cts:path-range-query(\"/tXML/Questions/Id\", \"=\", \""+questionSetId+"\"))))) and\n" +
                    "exists(cts:search(/tXML/Questions, cts:and-query((cts:collection-query(\"Questions\"),cts:path-range-query(\"/tXML/Questions/Question/questionId\", \"=\", \""+questionId+"\")))))))").trim()))
                throw new Exception("Invalid Question Number");
            return mlc.executeXQuery("let $a := cts:search(/tXML/Questions, cts:and-query((cts:collection-query(\"Questions\"),cts:path-range-query(\"/tXML/Questions/Id\", \"=\", \"" + questionSetId + "\"))))\n" +
                    "return (\n" +
                    "xdmp:node-replace($a/Question[questionId eq \"" + questionId + "\"]/questionId, element questionId{\"" + questionId + "\"}),\n" +
                    "xdmp:node-replace($a/Question[questionId eq \"" + questionId + "\"]/question, element question{\"" + q.getQuestion() + "\"}),\n" +
                    "xdmp:node-replace($a/Question[questionId eq \"" + questionId + "\"]/options, element options{" + getOptionElements(q.getOptions()) + "}),\n" +
                    "xdmp:node-replace($a/Question[questionId eq \"" + questionId + "\"]/answer, element answer{\"" + q.getAnswer() + "\"})\n" +
                    "),\n" +
                    "\"Question </" + questionSetId + "/" + questionId + "> Updated successfully\""
            );
        }
        catch(Exception e){
            return "Failed to Update Question </"+questionSetId+"/"+questionId+">  =>  "+"Error: "+e.getMessage()+"\n";
        }
    }


    public String deleteQuestionById(String questionSetId, String questionId) {
        try{
            return mlc.executeXQuery("let $question := cts:search(/tXML/Questions,cts:and-query((cts:collection-query(\"Questions\"),cts:path-range-query(\"/tXML/Questions/Id\", \"=\", \""+questionSetId+"\"))))/Question[questionId eq \""+questionId+"\"]\n" +
                    "return \n" +
                    "if(exists($question))\n" +
                    "then (xdmp:node-delete($question),\"Question </"+questionSetId+"/"+questionId+"> Deleted successfully\")\n" +
                    "else\n" +
                    "\"Question </"+questionSetId+"/"+questionId+"> doesn't exist\"");
        }
        catch (Exception e){
            return "Failed to Delete Question </"+questionSetId+"/"+questionId+">  =>  "+"Error: "+e.getMessage()+"\n";
        }
    }

    public String evaluate(String candidateId) {
        return mlc.executeXQuery("let $candidate := cts:search(/tXML/Candidate,cts:and-query((cts:collection-query(\"Candidate\"),cts:path-range-query(\"/tXML/Candidate/candidate_id\", \"=\", \""+candidateId+"\"))))\n" +
                "return\n" +
                "if(exists($candidate)) then\n" +
                "(\n" +
                "  if($candidate/isStarted eq true() and $candidate/isSubmit eq true()) then\n" +
                "  (" +
                "    let $_ := xdmp:node-replace($candidate/isStarted, element isStarted{false()})\n" +
                "    let $_ := xdmp:node-replace($candidate/isSubmit, element isSubmit{false()})\n" +
                "      let $marks :=\n" +
                "      let $count := (0)\n" +
                "      let $questionIds := cts:search(/tXML/Answer,cts:and-query((cts:collection-query(\"Answers\"),cts:path-range-query(\"/tXML/Answer/candidateId\", \"=\", $candidate/candidate_id))))/questionIds\n" +
                "      let $answers := cts:search(/tXML/Answer,cts:and-query((cts:collection-query(\"Answers\"),cts:path-range-query(\"/tXML/Answer/candidateId\", \"=\", $candidate/candidate_id))))/answers\n" +
                "      for $questionId at $x in $questionIds/questionId\n" +
                "      let $storedQuestionInDatabase := cts:search(/tXML/Questions/Question,cts:and-query((cts:collection-query(\"Questions\"),cts:path-range-query(\"/tXML/Questions/Question/questionId\", \"=\", $questionId))))\n" +
                "      let $_ := if($answers/answer[$x] eq $storedQuestionInDatabase/answer) then xdmp:set($count, $count + 1) else ()\n" +
                "      return $count\n" +
                "  let $score:= fn:max($marks)\n" +
                "  return \n" +
                "  (if($score > 6) then \n" +
                "      \"<\"||$candidate/candidate_id/string()||\"> : <\"||$candidate/name/string()||\"> is selected for next Round.&#10;\"\n" +
                "    else \n" +
                "      \"<\"||$candidate/candidate_id/string()||\"> : <\"||$candidate/name/string()||\"> is rejected in this Round.&#10;\")\n" +
                "      ||\"Correct Answer: \"||$score||\"&#10;\"\n" +
                "      ||\"Incorrect Answer: \"||(10 - $score)\n" +
                "    )\n" +
                "    else \"Exam is not submitted\"\n" +
                ")\n" +
                "else \"Candidate Id doesn’t exist\"");
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    static class Exception extends RuntimeException {
        public Exception(String message) {
            super(message);
        }
    }
}

