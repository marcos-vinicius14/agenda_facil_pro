package api.agendafacilpro.infraestructure.web.controllers;

import api.agendafacilpro.core.domain.shared.PaginatedResult;
import api.agendafacilpro.core.usecases.input.patient.ListPatientInput;
import api.agendafacilpro.core.usecases.input.patient.SoftDeletePatientInput;
import api.agendafacilpro.core.usecases.output.patient.CreatePatientOutput;
import api.agendafacilpro.core.usecases.output.patient.ListPatientOutput;
import api.agendafacilpro.core.usecases.patient.CreatePatientUseCase;
import api.agendafacilpro.core.usecases.patient.ListPatientUseCase;
import api.agendafacilpro.core.usecases.patient.SoftDeletePatientUseCase;
import api.agendafacilpro.infraestructure.web.dtos.request.CreatePatientRequest;
import api.agendafacilpro.infraestructure.web.dtos.response.PaginatedResponse;
import api.agendafacilpro.infraestructure.web.dtos.response.PatientResponse;
import api.agendafacilpro.infraestructure.web.presenters.PatientPresenter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patients", description = "Gestão completa de pacientes")
@SecurityRequirement(name = "bearerAuth")
public class PaatientController {
    private final CreatePatientUseCase createPatientUseCase;
    private final ListPatientUseCase listPatientUseCase;
    private final SoftDeletePatientUseCase softDeletePatientUseCase;


    public PaatientController(CreatePatientUseCase createPatientUseCase, ListPatientUseCase listPatientUseCase, SoftDeletePatientUseCase softDeletePatientUseCase) {
        this.createPatientUseCase = createPatientUseCase;
        this.listPatientUseCase = listPatientUseCase;
        this.softDeletePatientUseCase = softDeletePatientUseCase;
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar novo paciente", description = "Cria um novo paciente vinculado à organização do usuário logada.")
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody CreatePatientRequest request) {
        CreatePatientOutput createdPatient = createPatientUseCase.execute(request.toInput());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PatientPresenter.toResponse(createdPatient));
    }

    @GetMapping
    @Operation(summary = "Listar todos os pacientes encontrados na base.", description = "Retorna uma lista paginada de pacientes")
    public ResponseEntity<PaginatedResponse<PatientResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ListPatientInput input = new ListPatientInput(page, size);
        PaginatedResult<ListPatientOutput> paginatedResult = listPatientUseCase.execute(input);

        return  ResponseEntity
                .ok(PatientPresenter.toResponse(paginatedResult));


    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar paciente", description = "Realizar a exclusão lógica de um paciente")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        SoftDeletePatientInput input = new SoftDeletePatientInput(id);
        softDeletePatientUseCase.execute(input);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
