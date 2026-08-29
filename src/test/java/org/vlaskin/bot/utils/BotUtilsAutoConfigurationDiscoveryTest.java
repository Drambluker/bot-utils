package org.vlaskin.bot.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Map;

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

    @Test
    void rendersClasspathTemplateUsingAutoConfiguredEngine()
    {
        contextRunner.run(context -> assertThat(context.getBean(PebbleService.class)
                .renderTemplate("messages/welcome", Map.of("user", "Инвестор")))
                .isEqualTo("Здравствуйте, Инвестор!\n"));
    }

    @SpringBootConfiguration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class TestApplication
    {
    }
}
