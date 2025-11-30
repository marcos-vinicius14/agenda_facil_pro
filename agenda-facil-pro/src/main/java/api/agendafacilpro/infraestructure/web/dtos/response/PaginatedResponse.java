package api.agendafacilpro.infraestructure.web.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PaginatedResponse<T>(
        @Schema(description = "Lista de itens da página atual.")
        List<T> data,

        @Schema(description = "Total de itens encontrado na base de dados.")
        long totalItems,

        @Schema(description = "Total de páginas disponíveis.")
        int totalPages,

        @Schema(description = "Página atual (base)")
        int currentPage
) {
}
