package es.dsw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"es.dsw"})
public class Demo11ApiRestJwtAlumnosApplication {

	public static void main(String[] args) {
		SpringApplication.run(Demo11ApiRestJwtAlumnosApplication.class, args);
	}

}
