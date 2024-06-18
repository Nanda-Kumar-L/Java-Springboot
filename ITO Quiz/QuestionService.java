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

    public String createQuestion(Question q) {
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
                    "xdmp:document-insert(\"/itoquiz/Questions/\" || " + q.getQuestion_id() + ",\n" +
                            "  element tXML{" +
                            "    element Question{\n" +
                            "    element questionId{\"" + q.getQuestion_id() + "\"},\n" +
                            "    element question{\"" + q.getQuestion() + "\"},\n" +
                            "    element options{" + getOptionElements(q.getOptions()) + "},\n" +
                            "    element answer{\"" + q.getAnswer() + "\"}\n" +
                            "  } \n" +
                            "}, \n" +
                            "  <options xmlns=\"xdmp:document-insert\">\n" +
                            "  <collections>\n" +
                            "  <collection>Questions</collection>  \n" +
                            "  </collections>\n" +
                            "  </options>\n" +
                            "),\"Question <" + q.getQuestion_id() + "> generated successfully\""
            );
        }
        catch(Exception e){
            return "Failed to Generate Question <"+q.getQuestion_id()+">  =>  "+"Error: "+e.getMessage()+"\n";
        }
    }


    public String getAllQuestions() {
        try{
            mlc.executeXQuery("");
            return mlc.executeXQuery("""
                    import module namespace json="http://marklogic.com/xdmp/json" at "/MarkLogic/json/json.xqy";
                    if(count(cts:search(/tXML/Question,cts:collection-query("Questions"))) eq 0) then "No Question available"
                    else
                    (
                      let $_ := for $i in cts:search(/tXML/Question,cts:collection-query("Questions"))
                                let $json := map:map()
                                let $_:= map:put($json, "QuestionId", data($i/questionId))
                                let $_:= map:put($json, "Question", data($i/question))
                                let $_:= map:put($json, "Answer", data($i/answer))
                                let $_:= map:put($json, "options", data($i/options/option))
                                return xdmp:to-json($json)
                      return json:to-array($_)
                    )""");
        }
        catch (Exception e){
            return e.getMessage();
        }
    }

    public String getQuestionById(String id) {
        return mlc.executeXQuery("let $question := cts:search(/Question, cts:collection-query(\"Questions\"))[./questionId eq \""+id+"\"]\n" +
                "let $json := map:map()\n" +
                "let $_:= map:put($json, \"QuestionId\", data($question/questionId))\n" +
                "let $_:= map:put($json, \"Question\", data($question/question))\n" +
                "let $_:= map:put($json, \"Answer\", data($question/answer))\n" +
                "let $_:= map:put($json, \"options\", data($question/options/option))\n" +
                "return xdmp:to-json($json)");
    }

    public String updateQuestionById(String id, Question q) {
        try{
            if(q.getOptions()==null)
                throw new Exception("Please enter all the fields");
            if(q.getOptions().size()!=4)
                throw new Exception("Minimum 4 options required");
            if(q.getAnswer() <= 0 || q.getAnswer()>4)
                throw new Exception("Please enter between 1 to 4 for answer field");
            if(q.getQuestion() == null)
                throw new Exception("Please enter all the fields");
            if(Boolean.parseBoolean(mlc.executeXQuery("xs:string(exists(cts:search(/Question, cts:collection-query(\"Questions\"))[./questionId eq \""+id+"\"]))")))
                throw new Exception("Invalid Question Number");
            return mlc.executeXQuery("xdmp:node-replace(cts:search(/Question, cts:collection-query(\"Questions\"))[./questionId eq \""+id+"\"]/questionId, element questionId{\""+id+"\"}),\n" +
                    "xdmp:node-replace(cts:search(/Question, cts:collection-query(\"Questions\"))[./questionId eq \""+id+"\"]/question, element question{\""+q.getQuestion()+"\"}),\n" +
                    "xdmp:node-replace(cts:search(/Question, cts:collection-query(\"Questions\"))[./questionId eq \""+id+"\"]/options, element options{"+getOptionElements(q.getOptions())+"}),\n" +
                    "xdmp:node-replace(cts:search(/Question, cts:collection-query(\"Questions\"))[./questionId eq \""+id+"\"]/answer, element answer{\""+q.getAnswer()+"\"}),\n" +
                    "\"Question <" + id + "> Updated successfully\""
                    );
        }
        catch(Exception e){
            return "Failed to Update Question <"+id+">  =>  "+"Error: "+e.getMessage()+"\n";
        }
    }


}
