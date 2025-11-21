package api.agendafacilpro.infraestructure.web.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record AuthResponse(
        @Schema(description = "ID do Usuário criado/logado", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userId,

        @Schema(description = "ID da Organização vinculada", example = "987fcdeb-51a2-43c1-z987-123456789012")
        UUID organizationId,

        @Schema(description = "Email do usuário", example = "admin@clinica.com")
        String email,

        @Schema(description = "Token JWT de Acesso (Curta duração)", example = "eyJhbGciOiJIUzI1NiIsIn...")
        String accessToken,

        @Schema(description = "Token JWT de Refresh (Longa duração)", example = "eyJhbGciOiJIUzI1NiIsIn...")
        String refreshToken,

        @Schema(description = "Tipo do token para ser usado no Header Authorization", example = "Bearer")
        String tokenType
) {
    public AuthResponse {
        if (tokenType == null) {
            tokenType = "Bearer";
        }
    }
}