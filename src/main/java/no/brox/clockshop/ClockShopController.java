package no.brox.clockshop;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClockShopController {

  @GetMapping("/")
  public String index() {
    return "Greetings from Spring Boot! Feel the steel-toed boot of spring pressing down on your neck\n";
  }

  @PostMapping(
			value = "/checkout",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
  public ResponseEntity<Integer> checkout(
		@RequestBody List<String> ids
  ) {
    return new ResponseEntity<>(1, HttpStatus.OK);
  }
}
