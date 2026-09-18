package org.vlaskin.bot.utils;

import io.pebbletemplates.boot.autoconfigure.PebbleAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class BotUtilsAutoConfigurationTest
{
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    PebbleAutoConfiguration.class,
                    BotUtilsAutoConfiguration.class));

    @Test
    void registersPebbleService()
    {
        contextRunner.run(context -> assertThat(context).hasSingleBean(PebbleService.class));
    }

    @Test
    void keepsUserDefinedPebbleService()
    {
        contextRunner.withUserConfiguration(CustomConfiguration.class)
                .run(context -> assertThat(context)
                        .getBean(PebbleService.class)
                        .isSameAs(context.getBean(CustomConfiguration.class).pebbleService));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomConfiguration
    {
        private final PebbleService pebbleService = new PebbleService(
                new io.pebbletemplates.pebble.PebbleEngine.Builder().build());

        @Bean
        PebbleService pebbleService()
        {
            return pebbleService;
        }
    }
}
