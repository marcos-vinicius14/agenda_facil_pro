package api.agendafacilpro.infraestructure.web.presenters;

import api.agendafacilpro.core.usecases.output.RegisterClinicOutPut;
import api.agendafacilpro.infraestructure.web.dtos.AuthResponse;

public class AuthPresenter {

    public static AuthResponse toResponse(RegisterClinicOutPut output) {
        return new AuthResponse(
                output.userId(),
                output.organizationId(),
                output.email(),
                output.accessToken(),
                output.refreshToken(),
                "Bearer"
        );
    }
}