package nl.imine.vaccine.model;

import java.util.List;

public class InjectableDependency implements Dependency {

    private final Class type;
    private final List<Class> prerequisites;

    public InjectableDependency(Class type, List<Class> prerequisites) {
        this.type = type;
        this.prerequisites = prerequisites;
    }

    @Override
    public Class getType() {
        return type;
    }

    public List<Class> getPrerequisites() {
        return prerequisites;
    }

}
