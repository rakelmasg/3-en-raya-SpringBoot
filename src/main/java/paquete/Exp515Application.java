package paquete;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@SpringBootApplication
@EnableWebSocket
public class Exp515Application implements WebSocketConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(Exp515Application.class, args);
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(createGameHandler(), "/games")
			.setAllowedOrigins("*");
	}
	
	@Bean
	public GameHandler createGameHandler() {
		return new GameHandler();
	}

}

