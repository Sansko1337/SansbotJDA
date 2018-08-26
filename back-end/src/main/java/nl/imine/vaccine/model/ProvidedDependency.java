package nl.imine.vaccine.model;

import java.lang.reflect.Method;

public class ProvidedDependency implements Dependency {

    private final Class type;
    private final InjectableDependency providedBy;
    private final Method providingMethod;

    public ProvidedDependency(Class type, InjectableDependency providedBy, Method providingMethod) {
        this.type = type;
        this.providedBy = providedBy;
        this.providingMethod = providingMethod;
    }

    @Override
    public Class getType() {
        return type;
    }

    public InjectableDependency getProvidedBy() {
        return providedBy;
    }

    public Method getProvidingMethod() {
        return providingMethod;
    }
}
