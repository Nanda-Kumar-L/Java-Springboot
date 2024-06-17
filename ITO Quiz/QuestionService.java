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

    public String getOptionElements(List<String> options){
        StringBuilder sb = new StringBuilder();
        int count=0;
        for(String s : options){
            count++;
            if(count==options.size())
            {
                sb.append("element option{/\"").append(s).append("\"}");
                break;
            }
            sb.append("element option{\"").append(s).append("\"},");

        }
        return sb.toString();
    }


    public String createQuestion(Question q) {
        return mlc.executeXQuery(
                "xdmp:document-insert(\"/Question/\" || "+q.getQuestion_id()+", \n" +
                        "  element Questions{\n" +
                        "    element questionId{\""+q.getQuestion_id()+"\"},\n" +
                        "    element question{\""+q.getQuestion()+"\"},\n" +
                        "    element options{"+getOptionElements(q.getOptions())+"},\n" +
                        "    element answer{\""+q.getAnswer()+"\"}\n" +
                        "    \n" +
                        "  }, \n" +
                        "  <options xmlns=\"xdmp:document-insert\">\n" +
                        "  <collections>\n" +
                        "  <collection>Questions</collection>  \n" +
                        "  </collections>\n" +
                        "  </options>\n" +
                        "),\"Done\",fn:doc(\"/Question/4\")"
        );
    }
}
