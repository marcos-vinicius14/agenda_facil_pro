package api.agendafacilpro.infraestructure.config.jackson;

import api.agendafacilpro.core.domain.entities.Patient;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Patient.Builder.class)
public abstract class PatientMixIn {
    @JsonPOJOBuilder(withPrefix = "with", buildMethodName = "build")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PatientBuilderMixIn {
    }
}
