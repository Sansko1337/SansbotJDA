package nl.imine.vaccine;

import java.util.List;
import java.util.Optional;

public interface IVaccine {

    void inject(String basePackage);
    Optional<Object> getInjected(Class type);
    List<Object> getCandidates();
}

