package api.agendafacilpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.agendafacil.infrastructure.persistence")
public class AgendaFacilProApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgendaFacilProApplication.class, args);
    }
}