package api.agendafacilpro.infraestructure.web.presenters;

import api.agendafacilpro.core.usecases.output.RegisterClinicOutPut;
import api.agendafacilpro.infraestructure.web.dtos.response.AuthResponse;

public class AuthPresenter {

    public static AuthResponse toResponse(RegisterClinicOutPut output) {
        return new AuthResponse(
                output.userId(),
                output.organizationId(),
                output.email()
        );
    }
}