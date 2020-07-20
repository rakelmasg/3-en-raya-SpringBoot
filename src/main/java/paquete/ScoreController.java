package paquete;

import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/highscores")
public class ScoreController {
			
		
	@GetMapping
	public Collection<Score> highscores() {	
		return GameHandler.getScores();
	}
}


