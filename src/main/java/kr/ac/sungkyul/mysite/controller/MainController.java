package kr.ac.sungkyul.mysite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
	
	final String userid = "sky";


	@RequestMapping("{userid}/main")
	public String index(@PathVariable String userid) {
		
		System.out.println("userid " + userid);

		return "main/index";
	}

}