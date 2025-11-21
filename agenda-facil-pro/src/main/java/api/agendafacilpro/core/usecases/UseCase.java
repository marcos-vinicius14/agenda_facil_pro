package api.agendafacilpro.core.usecases;

public interface UseCase<I, O> {
    O execute(I input);
}
