package nl.imine.vaccine.testresources.circularcomplex;

import nl.imine.vaccine.IVaccine;
import nl.imine.vaccine.Vaccine;
import nl.imine.vaccine.VaccineTwoElectricBoogaloo;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.exception.CircularDependencyException;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class CircularComplexTest {

    private IVaccine vaccine;

    @Before
    public void setUp() throws Exception {
        vaccine = new VaccineTwoElectricBoogaloo(new Properties());
    }

    //Dependency circle A->B->C->E->D->A
    @Test(expected = CircularDependencyException.class)
    public void testPreventCircularDependenciesComplex() {
        vaccine.inject("nl.imine.vaccine.testresources.circularcomplex");
    }

    @Component
    public static class CircularPartA {
        public CircularPartA(CircularPartB part) {

        }
    }

    @Component
    private static class CircularPartB {
        public CircularPartB(CircularPartC part) {

        }
    }

    @Component
    private static class CircularPartC {
        public CircularPartC(CircularPartE part) {

        }
    }

    @Component
    private static class CircularPartD {

        public CircularPartD(CircularPartA part) {

        }
    }

    @Component
    private static class CircularPartE {

        public CircularPartE(CircularPartD part) {

        }
    }
}
