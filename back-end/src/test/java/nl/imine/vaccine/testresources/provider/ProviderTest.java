package nl.imine.vaccine.testresources.provider;

import nl.imine.vaccine.Vaccine;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Provided;
import nl.imine.vaccine.exception.UnknownDependencyException;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import static junit.framework.Assert.assertNotNull;

public class ProviderTest {

    private Vaccine vaccine;

    @Before
    public void setUp() throws Exception {
        vaccine = new Vaccine();
    }

    @Test
    public void testProviderAnnotation() {
        vaccine.inject(new Properties(), "nl.imine.vaccine.testresources.provider");
        assertNotNull(((ProviderTest.ParentB) vaccine.getInjected(ProviderTest.ParentB.class).orElseThrow(null)).getChildProvided());
    }

    @Component
    public static class ParentA {
        public ParentA(ChildInjected childInjected) {

        }
    }

    @Component
    public static class ParentB {
        private final ChildProvided childProvided;

        public ParentB(ChildProvided childProvided) {
            this.childProvided = childProvided;
        }

        public ChildProvided getChildProvided() {
            return childProvided;
        }
    }

    @Component
    public static class ChildInjected {
        @Provided
        public ChildProvided childProvided() {
            return new ChildProvided();
        }
    }

    public static class ChildProvided {
        private ChildProvided() {

        }
    }

}
