package api.agendafacilpro.core.usecases.patient;

import api.agendafacilpro.core.domain.entities.Patient;
import api.agendafacilpro.core.exceptions.ForbiddenException;
import api.agendafacilpro.core.exceptions.ValidationException;
import api.agendafacilpro.core.gateway.PatientGateway;
import api.agendafacilpro.core.multitenancy.TenantContext;
import api.agendafacilpro.core.usecases.UseCase;
import api.agendafacilpro.core.usecases.input.patient.SoftDeletePatientInput;
import api.agendafacilpro.core.usecases.output.patient.SoftDeletePatientOutput;

import java.util.UUID;

public class SoftDeletePatientUseCase implements UseCase<SoftDeletePatientInput, SoftDeletePatientOutput> {
    private final PatientGateway patientGateway;

    public SoftDeletePatientUseCase(PatientGateway patientGateway) {
        this.patientGateway = patientGateway;
    }

    @Override
    public SoftDeletePatientOutput execute(SoftDeletePatientInput input) {
        UUID currentTenantId = TenantContext.getTenantOrThrow();

        Patient existsPatient = patientGateway.findById(input.userId())
                .orElseThrow(() -> new ValidationException("Paciente não encontrado ou não existe."));

        if (!existsPatient.getOrganizationId().equals(currentTenantId)) {
            throw new ForbiddenException("Acesso negado para esse paciente");
        }

        Patient deactivatedPatient = existsPatient.deactivate();

        Patient updatedPatient = patientGateway.update(deactivatedPatient);

        return new SoftDeletePatientOutput(
                updatedPatient.getId(),
                updatedPatient.getIsActive()
        );
    }
}
