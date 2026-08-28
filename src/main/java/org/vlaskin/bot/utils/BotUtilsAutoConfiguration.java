package org.vlaskin.bot.utils;

import io.pebbletemplates.pebble.PebbleEngine;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/** Автоконфигурация общих сервисов для приложений ботов. */
@AutoConfiguration
public class BotUtilsAutoConfiguration
{
    /**
     * Регистрирует сервис рендеринга, если приложение не предоставило собственную реализацию.
     *
     * @param engine движок, настроенный Pebble starter
     * @return сервис рендеринга шаблонов
     */
    @Bean
    @ConditionalOnMissingBean
    public PebbleService pebbleService(PebbleEngine engine)
    {
        return new PebbleService(engine);
    }
}
