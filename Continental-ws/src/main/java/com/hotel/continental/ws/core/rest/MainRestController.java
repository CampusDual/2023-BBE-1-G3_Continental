package com.hotel.continental.ws.core.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
public class MainRestController {
	@GetMapping(value = "/main", produces = MediaType.APPLICATION_JSON_VALUE)
	public String main() {
		return "index";
	}
}
