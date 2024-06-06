package com.javaspringml.marklogicjavaconnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MarklogicjavaconnectionApplication {

	private MarkLogicConnection mlc;

	@Autowired
	public MarklogicjavaconnectionApplication(MarkLogicConnection mlc) {
		this.mlc = mlc;
	}
	public static void main(String[] args) {
		SpringApplication.run(MarklogicjavaconnectionApplication.class, args);
	}


	@RequestMapping(value = "/xml",produces = "application/xml")
	public String index(String... args) throws Exception {
		String data = mlc.executeXQuery(
				"element results{" +
				"xdmp:node-insert-child(doc(\"/aaa\")/tXML, <a>aaaaa</a>),\"NODE INSERTED\"" +
				"}\n");

		return data;
	}

	@RequestMapping("/notxml")
	public String data(String... args) throws Exception {
		String data = mlc.executeXQuery(
				"declare variable $nl := \"&#10;\";\n" +
						"declare variable $limit := 500;\n" +
						"\n" +
						"declare function local:loop($num as xs:integer,$count as xs:integer,$i as xs:integer,$line as xs:integer){\n" +
						"\n" +
						"      if($i=1 and $i<=$num) then\n" +
						"      (\n" +
						"        fn:string-join((xs:string($i),\"\"),$nl),\n" +
						"        local:loop($num,$count+2,$i+1,$line+3)\n" +
						"      )\n" +
						"      else if($i=$count and $i<=$num) then\n" +
						"      (\n" +
						"        fn:string-join((xs:string($i),\"\"),$nl),\n" +
						"        local:loop($num,$count+$line,$i+1,$line+1)\n" +
						"      )\n" +
						"      else if($i<=$num) then\n" +
						"      (\n" +
						"        fn:string-join((xs:string($i),\"\"),\" \"),\n" +
						"        local:loop($num,$count,$i+1,$line)\n" +
						"      )\n" +
						"      else\n" +
						"      ()\n" +
						"};" +
						"fn:string-join(local:loop($limit,1,1,0),\"\")" +
						"\n");

		return data;
	}
}
