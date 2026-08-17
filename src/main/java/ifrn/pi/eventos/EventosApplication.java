package ifrn.pi.eventos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventosApplication {

	public static void main(String[] args) {
		System.out.println("Chamou o método index");
		SpringApplication.run(EventosApplication.class, args);
	}

}
