package api.agendafacilpro.core.usecases.patient;

import api.agendafacilpro.core.domain.entities.Patient;
import api.agendafacilpro.core.domain.valueobjects.CpfCnpj;
import api.agendafacilpro.core.domain.valueobjects.Email;
import api.agendafacilpro.core.domain.valueobjects.Phone;
import api.agendafacilpro.core.exceptions.ValidationException;
import api.agendafacilpro.core.gateway.PatientGateway;
import api.agendafacilpro.core.multitenancy.TenantContext;
import api.agendafacilpro.core.usecases.UseCase;
import api.agendafacilpro.core.usecases.input.patient.CreatePatientInput;
import api.agendafacilpro.core.usecases.output.patient.CreatePatientOutput;
import api.agendafacilpro.infraestructure.security.annotations.HasPermission;

import java.util.UUID;

public class CreatePatientUseCase implements UseCase<CreatePatientInput, CreatePatientOutput> {

    private final PatientGateway patientGateway;

    public CreatePatientUseCase(PatientGateway patientGateway) {
        this.patientGateway = patientGateway;
    }


    @Override
    @HasPermission("PATIENT_CREATE")
    public CreatePatientOutput execute(CreatePatientInput input) {
        UUID tenantId = TenantContext.getTenantOrThrow();

        validateUniqueEmail(tenantId, input.email());
        validateUniqueDocument(tenantId, input.document());

        Patient patient = Patient.builder()
                .withOrganizationId(tenantId)
                .withName(input.name())
                .withEmail(new Email(input.email()))
                .withPhone(new Phone(input.phone()))
                .withDocument(new CpfCnpj(input.document()))
                .build();

        Patient savedPatient = patientGateway.create(patient);

        return new CreatePatientOutput(
                savedPatient.getId(),
                savedPatient.getName(),
                savedPatient.getEmail().getValue()
        );
    }

    private void validateUniqueEmail(UUID tenantId, String email) {
        if (patientGateway.existsByEmail(tenantId, email)) {
            throw new ValidationException("Já existe um paciente com esse e-mail.");
        }
    }

    private void validateUniqueDocument(UUID tenantId, String document) {
        String rawDocument = document.replaceAll("\\D", "");

        if (patientGateway.existsByDocument(tenantId, rawDocument)) {
            throw new ValidationException("Já existe um paciente com esse CPF/CNPJ.");
        }
    }
}
