package api.agendafacilpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "api.agendafacilpro.infraestructure.persistence.repository")
@EntityScan(basePackages = "api.agendafacilpro.infraestructure.persistence.entities")
public class AgendaFacilProApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgendaFacilProApplication.class, args);
    }
}