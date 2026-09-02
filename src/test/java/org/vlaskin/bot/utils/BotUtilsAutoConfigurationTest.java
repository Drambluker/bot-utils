package org.vlaskin.bot.utils;

import io.pebbletemplates.boot.autoconfigure.PebbleAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

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
}
