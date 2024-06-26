package com.javaspringml.marklogicjavaconnection.controller;


import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import org.springframework.stereotype.Service;

@Service
public class MarkLogicConnection {

    private final DatabaseClient client;
    private TextDocumentManager docManager;

    public MarkLogicConnection(){
        client = DatabaseClientFactory.newClient(
                        "localhost", 7500, "Documents",
                        new DatabaseClientFactory.DigestAuthContext("admin", "admin"));

        docManager = client.newTextDocumentManager();
    }

    public String executeXQuery(String xqueryScript) {
        ServerEvaluationCall call = client.newServerEval();
        call.xquery(xqueryScript);
        EvalResultIterator resultIterator = call.eval();

        StringBuilder result = new StringBuilder();
        while (resultIterator.hasNext()) {
            EvalResult evalResult = resultIterator.next();
            result.append(evalResult.getString()).append("\n");
        }
        return result.toString();
    }
}
