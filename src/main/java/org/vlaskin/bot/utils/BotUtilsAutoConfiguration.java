package org.vlaskin.bot.utils;

import io.pebbletemplates.pebble.PebbleEngine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotUtilsAutoConfiguration
{
    @Bean
    @ConditionalOnMissingBean
    public PebbleService pebbleService(PebbleEngine engine)
    {
        return new PebbleService(engine);
    }
}
