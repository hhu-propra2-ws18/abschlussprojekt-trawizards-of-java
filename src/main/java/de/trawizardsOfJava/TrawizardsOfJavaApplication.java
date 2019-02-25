package de.trawizardsOfJava;

import de.trawizardsOfJava.web.UploadController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@SpringBootApplication
@Configuration
public class TrawizardsOfJavaApplication {

	public static void main(String[] args) {
	//	new File(UploadController.uploadDirectory).mkdir();
		SpringApplication.run(TrawizardsOfJavaApplication.class, args);
	}

}

