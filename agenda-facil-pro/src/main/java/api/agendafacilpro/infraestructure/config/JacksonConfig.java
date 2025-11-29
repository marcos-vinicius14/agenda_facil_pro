package api.agendafacilpro.infraestructure.config;

import api.agendafacilpro.core.domain.entities.Organization;
import api.agendafacilpro.core.domain.entities.Patient;
import api.agendafacilpro.core.domain.entities.User;
import api.agendafacilpro.core.domain.valueobjects.Email;
import api.agendafacilpro.infraestructure.config.jackson.OrganizationMixin;
import api.agendafacilpro.infraestructure.config.jackson.PatientMixIn;
import api.agendafacilpro.infraestructure.config.jackson.UserMixin;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.modules(new JavaTimeModule());
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            builder.modulesToInstall(new JavaTimeModule());

            builder.mixIn(User.class, UserMixin.class);
            builder.mixIn(User.Builder.class, UserMixin.UserBuilderMixIn.class);
            builder.mixIn(Email.class, UserMixin.class);
            builder.mixIn(Organization.class, OrganizationMixin.class);

            builder.mixIn(Patient.class, PatientMixIn.class);
            builder.mixIn(Patient.Builder.class, PatientMixIn.PatientBuilderMixIn.class);
        };
    }
}