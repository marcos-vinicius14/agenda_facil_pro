package api.agendafacilpro.infraestructure.web.controllers;

import api.agendafacilpro.core.usecases.organization.RegisterClinicUseCase;
import api.agendafacilpro.core.usecases.output.RegisterClinicOutPut;
import api.agendafacilpro.infraestructure.web.dtos.AuthResponse;
import api.agendafacilpro.infraestructure.web.dtos.ErrorResponse; // Nosso DTO de erro
import api.agendafacilpro.infraestructure.web.dtos.RegisterRequest;
import api.agendafacilpro.infraestructure.web.presenters.AuthPresenter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints responsáveis pelo registro e login de clínicas e usuários")
public class AuthController {

    private final RegisterClinicUseCase registerUseCase;

    public AuthController(RegisterClinicUseCase registerUseCase) {
        this.registerUseCase = registerUseCase;
    }

    @PostMapping("/register")
    @Transactional
    @Operation(
            summary = "Registrar nova Clínica",
            description = "Cria uma nova organização e um usuário administrador inicial. Retorna tokens de acesso."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Clínica e Usuário criados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos (CNPJ incorreto, Senha curta) ou Duplicidade (Email/CNPJ já existem)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {

        var input = request.toUseCaseInput();

        RegisterClinicOutPut output = registerUseCase.execute(input);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AuthPresenter.toResponse(output));
    }
}