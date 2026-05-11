package microsim.integration;

import org.junit.jupiter.api.Test;

import microsim.integration.demo07.experiment.StartPersons;

public class IntegrationTest {
    @Test
    void integration() {
        var args = new String[0];
        StartPersons.main(args);

        // FIXME: what to check?
    }
}
