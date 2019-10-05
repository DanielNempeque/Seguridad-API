package co.edu.unisabana.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
	/**
	 * Test service
	 * @return
	 */
	@RequestMapping({ "/hello" })
	public String firstPage() {
		return "Hello World";
	}

}
