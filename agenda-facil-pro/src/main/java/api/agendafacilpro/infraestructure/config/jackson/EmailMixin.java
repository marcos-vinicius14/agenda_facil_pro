package api.agendafacilpro.infraestructure.config.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class EmailMixin {
    @JsonCreator
    EmailMixin(@JsonProperty("value") String value) {}
}
