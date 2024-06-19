package com.marklogicquery.run.xquery.service;

import com.marklogicquery.run.xquery.controller.MarkLogicConnection;
import com.marklogicquery.run.xquery.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
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
            if(Boolean.parseBoolean(mlc.executeXQuery("xs:string(exists(cts:search(/tXML/Questions, cts:and-query((cts:collection-query(\"Questions\"),cts:path-range-query(\"/tXML/Questions/Id\", \"=\", \""+questionSetId+"\"))))))")))
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

    public String updateQuestionSetById(String questionSetId, Question q) {
        return updateQuestionById(questionSetId,q.getQuestion_id(),q);
    }
}
