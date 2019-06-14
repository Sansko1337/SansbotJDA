package nl.imine.vaccine;

import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import nl.imine.vaccine.annotation.Provided;
import nl.imine.vaccine.exception.*;
import nl.imine.vaccine.model.ComponentDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class Vaccine {

    private static final Logger logger = LoggerFactory.getLogger(Vaccine.class);

    private List<Object> candidates = new ArrayList<>();
    private List<ComponentDependency> dependencies = new ArrayList<>();

    private Properties properties;

    public void inject(Properties properties, String basePackage) {
        this.properties = properties;

        logger.info("Initializing Injection");
        List<Class> classes = scanForComponentClasses(basePackage);

        dependencies = scanClassesForCandidates(classes);

        dependencies.forEach(dependency -> {
            if (isDependencyNotCreated(dependency)) {
                resolveDependency(dependency);
            }
        });

        logger.info("Found following components:");
        dependencies.forEach(dependency -> logger.info(dependency.getType().getName()));
    }

    private boolean isDependencyNotCreated(ComponentDependency dependency) {
        return dependencies.stream().noneMatch(candidate -> candidate.getClass().equals(dependency.getType()));
    }

    private List<Class> scanForComponentClasses(String basePackage) {
        try {
            return PackageScanner.getClassesForPackage(basePackage).stream().filter(c -> c.isAnnotationPresent(Component.class)).collect(Collectors.toList());
        } catch (ClassNotFoundException e) {
            throw new PackageLoadFailedException("Could not load packages", e);
        }
    }

    private List<ComponentDependency> scanClassesForCandidates(List<Class> classes) {
        return classes.stream()
                .map(this::getInjectionDetails)
                .collect(Collectors.toList());
    }

    private ComponentDependency getInjectionDetails(Class clazz) {
        Constructor[] constructors = clazz.getConstructors();
        if (constructors.length <= 1) {
            List<Class> foundDependencies = new ArrayList<>();
            for (Parameter parameter : constructors[0].getParameters()) {
                if (!parameter.isAnnotationPresent(Property.class)) {
                    foundDependencies.add(parameter.getType());
                }
            }
            return new ComponentDependency(clazz, foundDependencies.toArray(new Class[0]), getProvidedComponentsFromClass(clazz));
        }
        throw new ConstructorStalemateException(clazz);
    }

    private Class[] getProvidedComponentsFromClass(Class clazz) {
        return Arrays.stream(clazz.getMethods())
                .filter(method -> method.getParameterCount() == 0)
                .filter(method -> method.isAnnotationPresent(Provided.class))
                .map(Method::getReturnType)
                .toArray(Class[]::new);
    }

    private void resolveDependency(ComponentDependency dependency) {
        dependency.setObject(createOrGetCandidateInstance(dependency.getType(), new ArrayList<>()));
    }

    private Object createOrGetCandidateInstance(Class candidate, List<Class> parents) {
        return candidates.stream()
                .filter(createdInstance -> candidate.equals(createdInstance.getClass()))
                .findFirst()
                .orElseGet(() -> {
                    Object instance = createInstanceFromCandidate(candidate, parents);
                    candidates.add(instance);
                    runAfterCreation(instance);
                    return instance;
                });
    }

    private Object createInstanceFromCandidate(Class candidate, List<Class> parents) {
        if (parents.contains(candidate))
            throw new CircularDependencyException(parents, candidate);
        if (!classHasSingleConstructor(candidate))
            throw new ConstructorStalemateException(candidate);

        Constructor constructor = candidate.getConstructors()[0];
        try {
            return createObjectInstance(candidate, parents, constructor);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new DependencyInstantiationException("Could not create Instance for " + candidate.getName(), e);
        }
    }

    private Object createObjectInstance(Class candidate, List<Class> parents, Constructor constructor) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (constructorHasNoParameters(constructor))
            return constructor.newInstance();

        List<Object> constructorParameters = new ArrayList<>(constructor.getParameterCount());
        Arrays.stream(constructor.getParameters()).forEach(parameter -> {
            if (parameter.isAnnotationPresent(Property.class)) {
                constructorParameters.add(resolvePropertyDependency(parameter));
            } else {
                Class parameterType = parameter.getType();
                if (parameterType.isAnnotationPresent(Component.class)) {
                    parents.add(candidate);
                    constructorParameters.add(createOrGetCandidateInstance(parameterType, parents));
                } else {
                    Optional<Class> provider = getProvider(parameterType);
                    if (provider.isPresent()) {
                        parents.add(candidate);
                        Object providerInstance = createOrGetCandidateInstance(provider.get(), parents);
                        constructorParameters.add(searchAndCreateProviderInstance(providerInstance, parameterType));
                    } else {
                        throw new UnknownDependencyException(candidate, parameterType);
                    }
                }
            }
        });
        return constructor.newInstance(constructorParameters.toArray());
    }

    private boolean constructorHasNoParameters(Constructor constructor) {
        return constructor.getParameterCount() == 0;
    }

    private boolean classHasSingleConstructor(Class candidate) {
        return candidate.getConstructors().length == 1;
    }

    private Object searchAndCreateProviderInstance(Object providerInstance, Class requestedType) {
        return candidates.stream()
                .filter(requestedType::isInstance)
                .findFirst()
                .orElseGet(() -> Arrays.stream(providerInstance.getClass().getMethods())
                        .filter(method -> method.getParameterCount() == 0)
                        .filter(method -> method.isAnnotationPresent(Provided.class))
                        .filter(method -> method.getReturnType().equals(requestedType))
                        .map(method -> {
                            try {
                                Object providedObject = method.invoke(providerInstance);
                                candidates.add(providedObject);
                                return providedObject;
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                logger.error("Could not provide Instance for {}. Reason: ({}: {})",
                                        requestedType.getClass().getName(),
                                        e.getClass().getSimpleName(),
                                        e.getMessage());
                            }
                            return null;
                        })
                        .findFirst().orElse(null));
    }

    private String resolvePropertyDependency(Parameter parameter) {
        return (String) properties.get(parameter.getAnnotation(Property.class).value());
    }

    private void runAfterCreation(Object injectable) {
        Arrays.stream(injectable.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(AfterCreate.class))
                .filter(method -> method.getParameterCount() == 0)
                .forEach(method -> {
                    try {
                        method.invoke(injectable);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        logger.error("Could not run @PostConstruct for {}. {}", injectable.getClass().getName(), e);
                    }
                });
    }

    private Optional<Class> getProvider(Class type) {
        return dependencies.stream()
                .filter(dependency -> Arrays.stream(dependency.getProvidedClasses()).anyMatch(type::equals))
                .map(ComponentDependency::getType)
                .findAny();
    }


    public Optional<Object> getInjected(Class type) {
        return candidates.stream()
                .filter(candidate -> candidate.getClass().equals(type))
                .findAny();
    }

    public List<Object> getCandidates() {
        return candidates;
    }
}
