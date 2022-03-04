package io.dropwizard.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Application;
import io.dropwizard.core.Bundle;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class BundleTest {
    @Test
    void deprecatedBundleWillBeInitializedAndRun() throws Exception {
        final DeprecatedBundle deprecatedBundle = new DeprecatedBundle();
        assertThat(deprecatedBundle.wasInitialized()).isFalse();
        assertThat(deprecatedBundle.wasRun()).isFalse();

        final File configFile = File.createTempFile("bundle-test", ".yml");
        try {
            Files.write(configFile.toPath(), Arrays.asList(
                "text: Test",
                "server:",
                "  applicationConnectors:",
                "    - type: http",
                "      port: 0",
                "  adminConnectors:",
                "    - type: http",
                "      port: 0"
            ));

            final TestApplication application = new TestApplication(deprecatedBundle);
            application.run("server", configFile.getAbsolutePath());
        } finally {
            configFile.delete();
        }

        assertThat(deprecatedBundle.wasInitialized()).isTrue();
        assertThat(deprecatedBundle.wasRun()).isTrue();
    }

    private static class TestConfiguration extends Configuration {
        @JsonProperty
        String text = "";

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    @SuppressWarnings("deprecation")
    private static class TestApplication extends Application<TestConfiguration> {
        Bundle bundle;

        public TestApplication(Bundle bundle) {
            this.bundle = bundle;
        }

        @Override
        public void initialize(Bootstrap<TestConfiguration> bootstrap) {
            bootstrap.addBundle(bundle);
        }

        @Override
        public void run(TestConfiguration configuration, Environment environment) {
        }
    }

    @SuppressWarnings("deprecation")
    private static class DeprecatedBundle implements Bundle {
        boolean wasInitialized = false;
        boolean wasRun = false;

        @Override
        public void initialize(Bootstrap<?> bootstrap) {
            wasInitialized = true;
        }

        @Override
        public void run(Environment environment) {
            wasRun = true;
        }

        public boolean wasInitialized() {
            return wasInitialized;
        }

        public boolean wasRun() {
            return wasRun;
        }
    }
}
