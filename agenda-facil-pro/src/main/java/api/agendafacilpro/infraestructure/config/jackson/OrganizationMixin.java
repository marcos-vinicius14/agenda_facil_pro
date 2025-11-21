package api.agendafacilpro.infraestructure.config.jackson;

import api.agendafacilpro.core.domain.entities.Organization;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Organization.Builder.class)
public abstract class OrganizationMixin {
    @JsonPOJOBuilder(withPrefix = "with", buildMethodName = "build")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrganizationBuilderMixIn {
    }
}
