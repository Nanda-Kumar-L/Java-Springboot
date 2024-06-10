package com.javaspringml.marklogicjavaconnection.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@SpringBootApplication
@RestController
public class MarklogicjavaconnectionApplication {

	@Autowired
	private MarkLogicConnection mlc;
	@Autowired
	private QueryClass q;

	@Autowired
	public MarklogicjavaconnectionApplication(MarkLogicConnection mlc,QueryClass q) {
		this.mlc = mlc;
		this.q=q;
	}


	public static void main(String[] args) {
		SpringApplication.run(MarklogicjavaconnectionApplication.class, args);
	}

	@GetMapping("/execute-xquery")
	public String executeXQuery(@RequestParam("query") String query, Model model) {
		String result = mlc.executeXQuery(query);
		model.addAttribute("result", result);
		return result;
	}

	@RequestMapping(value = "/xml",produces = "application/xml")
	public String index(String... args) throws Exception {
		String data = mlc.executeXQuery(
				"element results{" +
				"xdmp:node-insert-child(doc(\"/aaa\")/tXML, <a>aaaaa</a>),\"NODE INSERTED\"" +
				"}\n");

		return data;
	}

	@GetMapping("/index")
	public String showIndex(Model model) {
		model.addAttribute("query", new QueryClass());
		return "index";  // Corresponds to index.html
	}

	@PostMapping("/submit")
	public String submitForm(@RequestParam("query") String query,
							 Model model) {
		// Add the form data to the model
		String result = mlc.executeXQuery(query);
		model.addAttribute("result", result);
		// You can add more logic here to process the form data

		return "result";  // Corresponds to result.html (a view to display the results)
	}

	@RequestMapping("/notxml")
	public String data(String... args) throws Exception {
		String res = mlc.executeXQuery(
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

		return res;
	}
}
