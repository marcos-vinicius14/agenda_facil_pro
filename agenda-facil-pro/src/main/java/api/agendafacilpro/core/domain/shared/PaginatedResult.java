package api.agendafacilpro.core.domain.shared;

import java.util.List;

public record PaginatedResult<T>(
        List<T> items,
        long totalItems,
        int totalPages,
        int currentPage
) {}