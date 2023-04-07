package no.brox.clockshop;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClockShopController {

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot! Feel the steel-toed boot of spring pressing down on your neck\n";
	}

	@PostMapping("/checkout")
	public void price() throws NotImplementedException {
		throw new NotImplementedException("TODO");
	}

}
