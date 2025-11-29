package api.agendafacilpro.core.domain.shared;

public record Pagination(int page, int size) {
    public Pagination {
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        if (size > 100) size = 100;
    }

    public static Pagination of(int page, int size) {
        return new Pagination(page, size);
    }
}
