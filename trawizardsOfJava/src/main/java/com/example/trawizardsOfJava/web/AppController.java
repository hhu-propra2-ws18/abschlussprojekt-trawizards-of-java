package com.example.trawizardsOfJava.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

	@GetMapping("/Benutzerverwaltung")
	public String benutzerverwaltung(){
		return "foo";
	}
}