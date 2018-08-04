package nl.imine.vaccine;

import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import nl.imine.vaccine.annotation.Provided;
import nl.imine.vaccine.exception.CircularDependencyException;
import nl.imine.vaccine.exception.ConstructorStalemateException;
import nl.imine.vaccine.exception.UnknownDependencyException;
import nl.imine.vaccine.model.ComponentDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Vaccine {

    private static Logger logger = LoggerFactory.getLogger(Vaccine.class);

    private List<Object> candidates = new ArrayList<>();
    private List<ComponentDependency> dependencies = new ArrayList<>();

    private Properties properties;

    /**
     * Private helper method
     *
     * @param directory
     *            The directory to start with
     * @param pckgname
     *            The package name to search for. Will be needed for getting the
     *            Class object.
     * @param classes
     *            if a file isn't loaded but still is in the directory
     * @throws ClassNotFoundException
     */
    private static void checkDirectory(File directory, String pckgname,
                                       ArrayList<Class<?>> classes) throws ClassNotFoundException {
        File tmpDirectory;

        if (directory.exists() && directory.isDirectory()) {
            final String[] files = directory.list();

            for (final String file : files) {
                if (file.endsWith(".class")) {
                    try {
                        classes.add(Class.forName(pckgname + '.'
                                + file.substring(0, file.length() - 6)));
                    } catch (final NoClassDefFoundError e) {
                        // do nothing. this class hasn't been found by the
                        // loader, and we don't care.
                    }
                } else if ((tmpDirectory = new File(directory, file))
                        .isDirectory()) {
                    checkDirectory(tmpDirectory, pckgname + "." + file, classes);
                }
            }
        }
    }

    /**
     * Private helper method.
     *
     * @param connection
     *            the connection to the jar
     * @param pckgname
     *            the package name to search for
     * @param classes
     *            the current ArrayList of all classes. This method will simply
     *            add new classes.
     * @throws ClassNotFoundException
     *             if a file isn't loaded but still is in the jar file
     * @throws IOException
     *             if it can't correctly read from the jar file.
     */
    private static void checkJarFile(JarURLConnection connection,
                                     String pckgname, ArrayList<Class<?>> classes)
            throws ClassNotFoundException, IOException {
        final JarFile jarFile = connection.getJarFile();
        final Enumeration<JarEntry> entries = jarFile.entries();
        String name;

        for (JarEntry jarEntry = null; entries.hasMoreElements()
                && ((jarEntry = entries.nextElement()) != null);) {
            name = jarEntry.getName();

            if (name.contains(".class")) {
                name = name.substring(0, name.length() - 6).replace('/', '.');

                if (name.contains(pckgname)) {
                    classes.add(Class.forName(name));
                }
            }
        }
    }

    /**
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader
     *
     * @param pckgname
     *            the package name to search
     * @return a list of classes that exist within that package
     * @throws ClassNotFoundException
     *             if something went wrong
     */
    public static ArrayList<Class<?>> getClassesForPackage(String pckgname)
            throws ClassNotFoundException {
        final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

        try {
            final ClassLoader cld = Thread.currentThread()
                    .getContextClassLoader();

            if (cld == null)
                throw new ClassNotFoundException("Can't get class loader.");

            final Enumeration<URL> resources = cld.getResources(pckgname
                    .replace('.', '/'));
            URLConnection connection;

            for (URL url = null; resources.hasMoreElements()
                    && ((url = resources.nextElement()) != null);) {
                try {
                    connection = url.openConnection();

                    if (connection instanceof JarURLConnection) {
                        checkJarFile((JarURLConnection) connection, pckgname,
                                classes);
                    } else {
                        try {
                            checkDirectory(
                                    new File(URLDecoder.decode(url.getPath(),
                                            "UTF-8")), pckgname, classes);
                        } catch (final UnsupportedEncodingException ex) {
                            throw new ClassNotFoundException(
                                    pckgname
                                            + " does not appear to be a valid package (Unsupported encoding)",
                                    ex);
                        }
                    }
                } catch (final IOException ioex) {
                    throw new ClassNotFoundException(
                            "IOException was thrown when trying to get all resources for "
                                    + pckgname, ioex);
                }
            }
        } catch (final NullPointerException ex) {
            throw new ClassNotFoundException(
                    pckgname
                            + " does not appear to be a valid package (Null pointer exception)",
                    ex);
        } catch (final IOException ioex) {
            throw new ClassNotFoundException(
                    "IOException was thrown when trying to get all resources for "
                            + pckgname, ioex);
        }

        return classes;
    }

    public static List<Class> findClassesInJar(File jar) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(jar)) {
            ZipInputStream zip = new ZipInputStream(fileInputStream);
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    // This ZipEntry represents a class. Now, what class does it represent?
                    String className = entry.getName().replace('/', '.'); // including ".class"
                    classes.add(Class.forName(className.substring(0, className.length() - 6)));
                }
            }
        } catch (IOException e) {
            logger.error("Exception while reading jar ({}: {})", e.getClass().getSimpleName(), e.getMessage());
        }
        return classes;
    }

    public void inject(Properties properties, String basePackage) {
        logger.info("Initializing Injection");
        this.properties = properties;

        //Load classes
        List<Class> classes = null;
        try {
            classes = getClassesForPackage(basePackage).stream().filter(c -> c.isAnnotationPresent(Component.class)).collect(Collectors.toList());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load packages");
        }

        logger.info("Found following components:");

        dependencies = scanClassesForCandidates(classes);

        dependencies.forEach(dependency -> {

            if (dependencies.stream().noneMatch(candidate -> candidate.getClass().equals(dependency.getType()))) {
                long resolveDependencyStartTimeMillis = System.currentTimeMillis();
                resolveDependency(dependency);
                logger.error("Resolving dependency " + dependency.getType().getName() + " took " + String.format("%,3d", (System.currentTimeMillis() - resolveDependencyStartTimeMillis)) +"ms");
            }
        });

        dependencies.forEach(dependency -> logger.info(dependency.getType().getName()));
    }

    public List<ComponentDependency> scanClassesForCandidates(List<Class> classes) {
        List<ComponentDependency> ret = new ArrayList<>();
        for (Class clazz : classes) {
            ret.add(getInjectionDetails(clazz));
        }
        return ret;
    }

    private ComponentDependency getInjectionDetails(Class clazz) {
        if (clazz.isAnnotationPresent(Component.class)) {
            Constructor[] constructors = clazz.getConstructors();
            if (constructors.length <= 1) {
                List<Class> dependencies = new ArrayList<>();
                for (Parameter parameter : constructors[0].getParameters()) {
                    if (!parameter.isAnnotationPresent(Property.class)) {
                        dependencies.add(parameter.getType());
                    }
                }
                return new ComponentDependency(clazz, dependencies.toArray(new Class[dependencies.size()]), getProvidedComponentsFromClass(clazz));
            }
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
        Optional<Object> instance = candidates.stream()
                .filter(createdInstance -> candidate.equals(createdInstance.getClass()))
                .findFirst();

        if (instance.isPresent()) {
            return instance.get();
        } else {
            if (!parents.contains(candidate)) {
                try {
                    Constructor[] constructors = candidate.getConstructors();
                    if (constructors.length == 1) {
                        Constructor constructor = constructors[0];
                        Parameter[] parameters = constructor.getParameters();
                        Object newInstance = null;
                        if (parameters.length == 0) {
                            newInstance = constructor.newInstance();
                        } else {
                            Object[] constructorParameterTypes = new Object[parameters.length];
                            for (int i = 0; i < parameters.length; i++) {
                                Parameter parameter = parameters[i];
                                if (constructor.getParameters()[i].isAnnotationPresent(Property.class)) {
                                    constructorParameterTypes[i] = resolvePropertyDependency(parameter);
                                } else {
                                    Class parameterType = parameter.getType();
                                    if (parameterType.isAnnotationPresent(Component.class)) {
                                        parents.add(candidate);
                                        constructorParameterTypes[i] = createOrGetCandidateInstance(parameterType, parents);
                                    } else {
                                        Class provider = getProvider(parameterType);
                                        if (provider != null) {
                                            parents.add(candidate);
                                            Object providerInstance = createOrGetCandidateInstance(provider, parents);
                                            constructorParameterTypes[i] = searchAndCreateProviderInstance(providerInstance, parameterType);
                                        } else {
                                            throw new UnknownDependencyException(candidate, parameterType);
                                        }
                                    }
                                }
                            }
                            newInstance = constructor.newInstance(constructorParameterTypes);
                        }
                        candidates.add(newInstance);
                        afterCreation(newInstance);
                        return newInstance;
                    }
                    throw new ConstructorStalemateException(candidate);
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    logger.error("Could not create Instance for {}. Reason: ({}: {})",
                            candidate.getClass().getName(),
                            e.getClass().getSimpleName(),
                            e.getMessage());
                    return null;
                }
            } else {
                throw new CircularDependencyException(parents, candidate);
            }
        }
    }

    private Object searchAndCreateProviderInstance(Object providerInstance, Class requestedType) {
        Optional<Object> instance = candidates.stream()
                .filter(requestedType::isInstance)
                .findFirst();

        return instance.orElseGet(() -> Arrays.stream(providerInstance.getClass().getMethods())
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
        Property property = parameter.getAnnotation(Property.class);
        return (String) properties.get(property.value());
    }

    private void afterCreation(Object injectable) {
        for (Method method : injectable.getClass().getMethods()) {
            if (method.getParameterCount() == 0 && method.isAnnotationPresent(AfterCreate.class)) {
                try {
                    method.invoke(injectable);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error("Could not run @PostConstruct for {}. Reason: ({}: {})",
                            injectable.getClass().getName(),
                            e.getClass().getSimpleName(),
                            e.getMessage());
                }
            }
        }
    }

    public Class getProvider(Class type) {
        for (ComponentDependency dependency : dependencies) {
            for (Class providerClass : dependency.getProvidedClasses()) {
                if (providerClass.equals(type)) {
                    return dependency.getType();
                }
            }
        }
        return null;
    }
}
