package nl.imine.vaccine.testresources.property;

import nl.imine.vaccine.Vaccine;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class PropertyTest {

    private Vaccine vaccine;

    @Before
    public void setUp() {
        vaccine = new Vaccine();
    }

    @Test
    public void testPropertyResolver() {
        Properties properties = new Properties();
        String parentProperty = "parentProperty";
        String childProperty = "childProperty";
        properties.setProperty("parent", parentProperty);
        properties.setProperty("child", childProperty);
        vaccine.inject(properties, "nl.imine.vaccine.testresources.property");

        PropertyTest.PropertyHolderParent propertyHolderParent = (PropertyTest.PropertyHolderParent) vaccine.getInjected(PropertyTest.PropertyHolderParent.class).orElseThrow(NullPointerException::new);
        PropertyTest.PropertyHolderChild propertyHolderChild = (PropertyTest.PropertyHolderChild) vaccine.getInjected(PropertyTest.PropertyHolderChild.class).orElseThrow(NullPointerException::new);

        assertNotNull(propertyHolderParent);
        assertNotNull(propertyHolderChild);

        assertEquals(propertyHolderChild, propertyHolderParent.getPropertyHolderChild());

        assertEquals(parentProperty, propertyHolderParent.getProperty());
        assertEquals(childProperty, propertyHolderChild.getProperty());
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
