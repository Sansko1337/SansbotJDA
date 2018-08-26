package nl.imine.vaccine.testresources.property;

import nl.imine.vaccine.IVaccine;
import nl.imine.vaccine.Vaccine;
import nl.imine.vaccine.VaccineTwoElectricBoogaloo;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class PropertyTest {

    private IVaccine vaccine;
    public static final String PARENT_PROPERTY = "parentProperty";
    public static final String CHILD_PROPERTY = "childProperty";

    @Before
    public void setUp() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("parent", PARENT_PROPERTY);
        properties.setProperty("child", CHILD_PROPERTY);

        vaccine = new VaccineTwoElectricBoogaloo(properties);
    }

    @Test
    public void testPropertyResolver() {

        vaccine.inject("nl.imine.vaccine.testresources.property");

        PropertyTest.PropertyHolderParent propertyHolderParent = (PropertyTest.PropertyHolderParent) vaccine.getInjected(PropertyTest.PropertyHolderParent.class).orElse(null);
        PropertyTest.PropertyHolderChild propertyHolderChild = (PropertyTest.PropertyHolderChild) vaccine.getInjected(PropertyTest.PropertyHolderChild.class).orElse(null);

        assertNotNull(propertyHolderParent);
        assertNotNull(propertyHolderChild);

        assertEquals(propertyHolderChild, propertyHolderParent.getPropertyHolderChild());

        assertEquals(PARENT_PROPERTY, propertyHolderParent.getProperty());
        assertEquals(CHILD_PROPERTY, propertyHolderChild.getProperty());
    }

    @Component
    public static class PropertyHolderParent {
        private final PropertyHolderChild propertyHolderChild;
        private final String property;

        public PropertyHolderParent(PropertyHolderChild propertyHolderChild, @Property("parent") String property) {
            this.propertyHolderChild = propertyHolderChild;
            this.property = property;
        }

        public PropertyHolderChild getPropertyHolderChild() {
            return propertyHolderChild;
        }

        public String getProperty() {
            return property;
        }
    }

    @Component
    public static class PropertyHolderChild {
        private final String property;

        public PropertyHolderChild(@Property("child") String property) {
            this.property = property;
        }

        public String getProperty() {
            return property;
        }
    }
}
