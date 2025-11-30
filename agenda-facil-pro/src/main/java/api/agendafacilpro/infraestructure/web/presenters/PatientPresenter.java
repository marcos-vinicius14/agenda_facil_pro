package api.agendafacilpro.infraestructure.web.presenters;

import api.agendafacilpro.core.domain.shared.PaginatedResult;
import api.agendafacilpro.core.usecases.output.patient.CreatePatientOutput;
import api.agendafacilpro.core.usecases.output.patient.ListPatientOutput;
import api.agendafacilpro.infraestructure.web.dtos.response.PaginatedResponse;
import api.agendafacilpro.infraestructure.web.dtos.response.PatientResponse;

import java.util.List;

public class PatientPresenter {

    public static PatientResponse toResponse(CreatePatientOutput output) {
        return new PatientResponse(
                output.patientId(),
                output.name(),
                output.email(),
                true
        );
    }

    public static PaginatedResponse<PatientResponse> toResponse(PaginatedResult<ListPatientOutput> result) {
        List<PatientResponse> items = result.items().stream()
                .map(item -> new PatientResponse(
                        item.id(),
                        item.name(),
                        item.email(),
                        item.isActive()
                ))
                .toList();

        return new PaginatedResponse<>(
                items,
                result.totalItems(),
                result.totalPages(),
                result.currentPage()
        );
    }
}