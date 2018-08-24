package nl.imine.vaccine.testresources.unkowndependency;

import nl.imine.vaccine.Vaccine;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.exception.UnknownDependencyException;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class UnkownDependencyTest {

    private Vaccine vaccine;

    @Test(expected = UnknownDependencyException.class)
    public void testUnknownDependencies() {
        vaccine.inject(new Properties(), "nl.imine.vaccine.testresources.unkowndependency");
    }

    @Before
    public void setUp() throws Exception {
        vaccine = new Vaccine();
    }

    @Component
    public static class KnownComponent {
        public KnownComponent(UnknownComponent unknownComponent) {

        }
    }

    private static class UnknownComponent {
    }
}
