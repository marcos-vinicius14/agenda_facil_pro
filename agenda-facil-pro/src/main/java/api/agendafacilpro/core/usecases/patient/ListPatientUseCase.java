package api.agendafacilpro.core.usecases.patient;

import api.agendafacilpro.core.domain.entities.Patient;
import api.agendafacilpro.core.domain.shared.PaginatedResult;
import api.agendafacilpro.core.domain.shared.Pagination;
import api.agendafacilpro.core.gateway.PatientGateway;
import api.agendafacilpro.core.multitenancy.TenantContext;
import api.agendafacilpro.core.usecases.UseCase;
import api.agendafacilpro.core.usecases.input.ListPatientInput;
import api.agendafacilpro.core.usecases.output.ListPatientOutput;
import api.agendafacilpro.infraestructure.security.annotations.HasPermission;

import java.util.List;
import java.util.UUID;

public class ListPatientUseCase implements UseCase<ListPatientInput, PaginatedResult<ListPatientOutput>> {

    private final PatientGateway patientGateway;

    public ListPatientUseCase(PatientGateway patientGateway) {
        this.patientGateway = patientGateway;
    }


    @Override
    @HasPermission("PATIENT_READ_ALL")
    public PaginatedResult<ListPatientOutput> execute(ListPatientInput input) {
        UUID tenantId = TenantContext.getTenantOrThrow();

        Pagination pagination = Pagination.of(input.page(), input.size());

        PaginatedResult<Patient> result = patientGateway.findAllByOrganizationId(tenantId, pagination);

        List<ListPatientOutput> outputItems = result.items().stream()
                .map(this::toOutput)
                .toList();

        return new PaginatedResult<>(
                outputItems,
                result.totalItems(),
                result.totalPages(),
                result.currentPage()
        );
    }


    private ListPatientOutput toOutput(Patient patient) {
        return new ListPatientOutput(
                patient.getId(),
                patient.getName(),
                patient.getEmail() != null ? patient.getEmail().getValue() : null,
                patient.getPhone() != null ? patient.getPhone().getValue() : null,
                patient.getDocument() != null ? patient.getDocument().getValue() : null,
                patient.getIsActive()
        );
    }


}
