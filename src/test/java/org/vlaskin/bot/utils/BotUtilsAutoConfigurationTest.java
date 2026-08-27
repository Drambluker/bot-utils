package org.vlaskin.bot.utils;

import io.pebbletemplates.pebble.PebbleEngine;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class BotUtilsAutoConfigurationTest
{
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(PebbleEngine.class, () -> new PebbleEngine.Builder().build())
            .withConfiguration(AutoConfigurations.of(BotUtilsAutoConfiguration.class));

    @Test
    void registersPebbleService()
    {
        contextRunner.run(context -> assertThat(context).hasSingleBean(PebbleService.class));
    }
}
