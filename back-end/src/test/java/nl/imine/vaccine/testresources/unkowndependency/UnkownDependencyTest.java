package nl.imine.vaccine.testresources.unkowndependency;

import nl.imine.vaccine.IVaccine;
import nl.imine.vaccine.VaccineTwoElectricBoogaloo;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.exception.UnknownDependencyException;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class UnkownDependencyTest {

    private IVaccine vaccine;

    @Before
    public void setUp() throws Exception {
        vaccine = new VaccineTwoElectricBoogaloo(new Properties());
    }

    @Test(expected = UnknownDependencyException.class)
    public void testUnknownDependencies() {
        vaccine.inject("nl.imine.vaccine.testresources.unkowndependency");
    }

    @Component
    public static class KnownComponent {
        public KnownComponent(UnknownComponent unknownComponent) {

        }
    }

    private static class UnknownComponent {
    }
}
