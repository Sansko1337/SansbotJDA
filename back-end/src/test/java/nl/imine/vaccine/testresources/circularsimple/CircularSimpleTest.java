package nl.imine.vaccine.testresources.circularsimple;

import nl.imine.vaccine.Vaccine;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.exception.CircularDependencyException;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class CircularSimpleTest {

    private Vaccine vaccine;

    @Before
    public void setUp() throws Exception {
        vaccine = new Vaccine();
    }

    @Test(expected = CircularDependencyException.class)
    public void testPreventCircularDependenciesSimple() {
        vaccine.inject(new Properties(), "nl.imine.vaccine.testresources.circularsimple");
    }

    @Component
    public static class CircularHalf {
        public CircularHalf(CircularOtherHalf otherHalf) {

        }
    }

    @Component
    private static class CircularOtherHalf {
        public CircularOtherHalf(CircularHalf half) {

        }
    }
}
