package org.vlaskin.bot.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class BotUtilsAutoConfigurationDiscoveryTest
{
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestApplication.class);

    @Test
    void discoversAutoConfigurationFromImportsFile()
    {
        contextRunner.run(context -> assertThat(context).hasSingleBean(PebbleService.class));
    }

    @SpringBootConfiguration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class TestApplication
    {
    }
}
