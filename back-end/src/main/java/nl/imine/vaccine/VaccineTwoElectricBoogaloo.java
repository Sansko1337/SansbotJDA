package nl.imine.vaccine;

import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Provided;
import nl.imine.vaccine.exception.ConstructorStalemateException;
import nl.imine.vaccine.exception.PackageLoadFailedException;
import nl.imine.vaccine.model.Dependency;
import nl.imine.vaccine.model.InjectableDependency;
import nl.imine.vaccine.model.ProvidedDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class VaccineTwoElectricBoogaloo implements IVaccine {

    private static final Logger logger = LoggerFactory.getLogger(VaccineTwoElectricBoogaloo.class);

    private final Properties properties;

    public VaccineTwoElectricBoogaloo(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void inject(String basePackage) {
        List<Class> classes = scanForComponentClasses(basePackage);

        List<Dependency> dependencies = readDependencyInformationFromClasses(classes);
        dependencies.forEach(dependency -> System.out.println(dependency.getType() + ": " + dependency.getClass().getName()));
    }


    @Override
    public Optional<Object> getInjected(Class type) {
        return Optional.empty();
    }

    @Override
    public List<Object> getCandidates() {
        return null;
    }

    private List<Class> scanForComponentClasses(String basePackage) {
        try {
            return PackageScanner.getClassesForPackage(basePackage).stream().filter(c -> c.isAnnotationPresent(Component.class)).collect(Collectors.toList());
        } catch (ClassNotFoundException e) {
            throw new PackageLoadFailedException("Could not load packages", e);
        }
    }

    private List<Dependency> readDependencyInformationFromClasses(List<Class> classes) {
        return classes.stream()
                .map(this::readDependencyInformationFromClass)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Dependency> readDependencyInformationFromClass(Class clazz) {
        List<Dependency> dependencyInformation = new ArrayList<>();

        if(clazz.getConstructors().length != 1)
            throw new ConstructorStalemateException(clazz);

        InjectableDependency injectableDependency = new InjectableDependency(clazz, getRequiredTypesFromConstructor(clazz.getConstructors()[0]));

        dependencyInformation.add(injectableDependency);
        dependencyInformation.addAll(getDependenciesProvidedByClass(injectableDependency));

        return dependencyInformation;
    }

    private Collection<? extends Dependency> getDependenciesProvidedByClass(InjectableDependency injectableDependency) {
        return Arrays.stream(injectableDependency.getType().getMethods())
                .filter(method -> method.getParameterCount() == 0)
                .filter(method -> method.isAnnotationPresent(Provided.class))
                .map(method -> new ProvidedDependency(method.getReturnType(), injectableDependency, method))
                .collect(Collectors.toList());
    }

    private List<Class> getRequiredTypesFromConstructor(Constructor constructor) {
        return Arrays.asList(constructor.getParameterTypes());
    }
}
