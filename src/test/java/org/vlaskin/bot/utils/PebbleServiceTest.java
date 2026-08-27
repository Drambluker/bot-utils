package org.vlaskin.bot.utils;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.StringLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PebbleServiceTest
{
    private final PebbleService pebbleService = new PebbleService(new PebbleEngine.Builder()
            .loader(new StringLoader())
            .build());

    @Test
    void rendersTemplateWithoutContext() throws IOException
    {
        assertThat(pebbleService.renderTemplate("Static text")).isEqualTo("Static text");
    }

    @Test
    void rendersTemplateWithContext() throws IOException
    {
        String rendered = pebbleService.renderTemplate(
                "Hello, {{ name }}!", Map.of("name", "Investor"));

        assertThat(rendered).isEqualTo("Hello, Investor!");
    }
}
