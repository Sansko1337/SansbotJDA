package ooo.sansk.sansbot.configuration;

import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Provided;

import java.time.Clock;

@Component
public class TimeProvider {

    @Provided
    public Clock provideClock() {
        return Clock.systemDefaultZone();
    }
}
