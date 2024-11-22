package lab.bookstore.catalogservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lab.bookstore.catalogservice.config.LineProperties;

@RestController
public class HomeController {
	
	@Autowired
	private LineProperties lineProperties;
	@GetMapping("/")
	public String getGreeting() {
		return lineProperties.getGreeting();
	}
	
	@Autowired
	private Environment env;
	@GetMapping("/port1")
	public String getPort1() {
		return env.getProperty("server.port");
	}
	
	@Value("${server.port}")
	private String serverPort;
	@GetMapping("/port2")
	public String getPort2() {
		return serverPort;
	}

}
