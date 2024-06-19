package com.marklogicquery.run.xquery.service;

import com.marklogicquery.run.xquery.controller.MarkLogicConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PathRangeIndex {

    @Autowired
    MarkLogicConnection mlc;

    public String createPathRangeIndexes() {
        return mlc.executeXQuery("import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";\n" +
                "let $config := admin:get-configuration()\n" +
                "let $dbid := xdmp:database(\"Documents\")\n" +
                "let $pri := admin:database-range-path-index($dbid,\"string\",\"/tXML/Questions/Id\",\"http://marklogic.com/collation/\",fn:false(),\"ignore\")\n" +
                "let $apri := admin:database-add-range-path-index($config, $dbid, $pri)\n" +
                "return admin:save-configuration($apri)," +
                "let $config := admin:get-configuration()\n" +
                "let $dbid := xdmp:database(\"Documents\")\n" +
                "let $pri := admin:database-range-path-index($dbid,\"string\",\"/tXML/Questions/Question/questionId\",\"http://marklogic.com/collation/\",fn:false(),\"ignore\")\n" +
                "let $apri := admin:database-add-range-path-index($config, $dbid, $pri)\n" +
                "return admin:save-configuration($apri)," +
                "\"Path Range Indexes Successfully Created\"");
    }
}
