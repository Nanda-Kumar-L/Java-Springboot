package com.marklogicquery.run.xquery.repository;

import com.marklogicquery.run.xquery.service.MarkLogicConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PathRangeIndexRepository {

    @Autowired
    MarkLogicConnection mlc;

    public void createPathRangeIndexes() {
         mlc.executeXQuery("""
                 import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
                 try{\
                 let $config := admin:get-configuration()
                 let $dbid := xdmp:database("Documents")
                 let $pri := admin:database-range-path-index($dbid,"string","/tXML/Questions/Id","http://marklogic.com/collation/",fn:false(),"ignore")
                 let $apri := admin:database-add-range-path-index($config, $dbid, $pri)
                 return admin:save-configuration($apri)}
                 catch($e){},\
                 try{
                 let $config := admin:get-configuration()
                 let $dbid := xdmp:database("Documents")
                 let $pri := admin:database-range-path-index($dbid,"string","/tXML/Questions/Question/questionId","http://marklogic.com/collation/",fn:false(),"ignore")
                 let $apri := admin:database-add-range-path-index($config, $dbid, $pri)
                 return admin:save-configuration($apri)}
                 catch($e){},\
                 try{
                 let $config := admin:get-configuration()
                 let $dbid := xdmp:database("Documents")
                 let $pri := admin:database-range-path-index($dbid,"string","/tXML/Candidate/candidate_id","http://marklogic.com/collation/",fn:false(),"ignore")
                 let $apri := admin:database-add-range-path-index($config, $dbid, $pri)
                 return admin:save-configuration($apri)}
                 catch($e){},\
                 try{
                 let $config := admin:get-configuration()
                 let $dbid := xdmp:database("Documents")
                 let $pri := admin:database-range-path-index($dbid,"string","/tXML/Answer/Id","http://marklogic.com/collation/",fn:false(),"ignore")
                 let $apri := admin:database-add-range-path-index($config, $dbid, $pri)
                 return admin:save-configuration($apri)}
                 catch($e){},\
                 try{
                 let $config := admin:get-configuration()
                 let $dbid := xdmp:database("Documents")
                 let $pri := admin:database-range-path-index($dbid,"string","/tXML/Answer/candidateId","http://marklogic.com/collation/",fn:false(),"ignore")
                 let $apri := admin:database-add-range-path-index($config, $dbid, $pri)
                 return admin:save-configuration($apri)}\
                 catch($e){},"Path Range Indexes Successfully Created"
                 """);
         System.out.println("Path Range Indexes Successfully Created");
    }
}
