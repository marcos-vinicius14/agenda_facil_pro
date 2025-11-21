package api.agendafacilpro.infraestructure.config.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CpfCnpjMixin {
    @JsonCreator
    CpfCnpjMixin(@JsonProperty("value") String value) {}
}
