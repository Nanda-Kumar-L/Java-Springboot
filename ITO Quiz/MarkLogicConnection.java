package com.marklogicquery.run.xquery.service;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import org.springframework.stereotype.Service;

@Service
public class MarkLogicConnection {

    private final DatabaseClient client;

    public MarkLogicConnection() {
        client = DatabaseClientFactory.newClient(
                "localhost", 7500, "Documents",
                new DatabaseClientFactory.DigestAuthContext("admin", "admin"));
    }

    public String executeXQuery(String xqueryScript) {
        try {
            ServerEvaluationCall call = client.newServerEval();
            call.xquery(xqueryScript);
            EvalResultIterator resultIterator = call.eval();

            StringBuilder result = new StringBuilder();
            while (resultIterator.hasNext()) {
                EvalResult evalResult = resultIterator.next();
                result.append(evalResult.getString()).append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            return "Error executing XQuery: " + e.getMessage()+"\n";
        }
    }
}
