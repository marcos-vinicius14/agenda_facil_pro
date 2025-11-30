package api.agendafacilpro.infraestructure.web.controllers;

import api.agendafacilpro.core.usecases.organization.RegisterClinicUseCase;
import api.agendafacilpro.core.usecases.output.RegisterClinicOutPut;
import api.agendafacilpro.infraestructure.web.dtos.response.AuthResponse;
import api.agendafacilpro.infraestructure.web.dtos.response.ErrorResponse;
import api.agendafacilpro.infraestructure.web.dtos.request.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints responsáveis pelo registro e login de clínicas e usuários")
public class AuthController {

    @Value("${api.security.token.access-token-expiration}")
    private Long accessTokenExpirationMs;

    @Value("${api.security.token.refresh-token-expiration}")
    private Long refreshTokenExpirationMs;

    @Value("${api.security.cookie.secure:false}")
    private boolean cookieSecure;

    private final RegisterClinicUseCase registerUseCase;

    public AuthController(RegisterClinicUseCase registerUseCase) {
        this.registerUseCase = registerUseCase;
    }

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

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        var input = request.toUseCaseInput();
        RegisterClinicOutPut output = registerUseCase.execute(input);

        ResponseCookie accessCookie = ResponseCookie.from("access_token", output.accessToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ofMillis(accessTokenExpirationMs))
                .sameSite("Strict")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", output.refreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ofMillis(refreshTokenExpirationMs))
                .sameSite("Strict")
                .build();

        AuthResponse responseBody = new AuthResponse(
                output.userId(),
                output.organizationId(),
                output.email()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(responseBody);
    }

    @Operation(
            summary = "Logout de usuário",
            description = "Cria um token de morte para invalidar o token do cookie"
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteAccessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        ResponseCookie deleteRefreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();


        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString())
                .build();
    }
}