package nl.imine.vaccine.testresources.complextree;

import nl.imine.vaccine.Vaccine;
import nl.imine.vaccine.annotation.Component;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ComplexTreeTest {

    private Vaccine vaccine;

    @Before
    public void setUp() throws Exception {
        vaccine = new Vaccine();
    }

    @Test
    public void testComplexTreeNoDuplicated() {
        vaccine.inject(new Properties(), "nl.imine.vaccine.testresources.complextree");
        Map<Class, Long> occurrences = vaccine.getCandidates().stream().collect(Collectors.groupingBy(Object::getClass, Collectors.counting()));
        occurrences.forEach((k, v) -> assertEquals(1, (long) v));
    }


    @Component
    public static class ParentA {
        public ParentA(ChildA childA, ChildB childB) {
        }
    }

    @Component
    public static class ParentB {
        public ParentB(ChildC childC) {
        }
    }

    @Component
    public static class ChildA {
        public ChildA(SharedChild sharedChild) {
        }
    }

    @Component
    public static class ChildB {
        public ChildB(SharedChild sharedChild) {
        }
    }

    @Component
    public static class ChildC {
        public ChildC(SharedChild sharedChild) {
        }
    }

    @Component
    public static class SharedChild {

    }
}
