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

	@RequestMapping(value = "/", produces = "application/xml")
	public String data(String... args) throws Exception {
		String data = mlc.executeXQuery(
				"element result{" +
				"cts:search(fn:doc(),cts:path-range-query(\n" +
				"   'collection(\"Books\")/tXML/Books/Pages',\n" +
				"   \"<\",\n" +
				"   500\n" +
				"))/node()" +
				"}\n");

		return data;
	}
}
