package nl.imine.vaccine.testresources.circularsimple;

import nl.imine.vaccine.IVaccine;
import nl.imine.vaccine.Vaccine;
import nl.imine.vaccine.VaccineTwoElectricBoogaloo;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.exception.CircularDependencyException;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class CircularSimpleTest {

    private IVaccine vaccine;

    @Before
    public void setUp() throws Exception {
        vaccine = new VaccineTwoElectricBoogaloo(new Properties());
    }

    @Test(expected = CircularDependencyException.class)
    public void testPreventCircularDependenciesSimple() {
        vaccine.inject("nl.imine.vaccine.testresources.circularsimple");
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
