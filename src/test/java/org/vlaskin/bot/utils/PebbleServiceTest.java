package org.vlaskin.bot.utils;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.StringLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        Map<String, String> context = Map.of("name", "Investor");
        String rendered = pebbleService.renderTemplate("Hello, {{ name }}!", context);

        assertThat(rendered).isEqualTo("Hello, Investor!");
    }

    @Test
    void rejectsMissingEngine()
    {
        assertThatThrownBy(() -> new PebbleService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Pebble engine must not be null");
    }

    @Test
    void rejectsBlankTemplateName()
    {
        assertThatThrownBy(() -> pebbleService.renderTemplate("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Template name must not be blank");
    }

    @Test
    void rejectsMissingContext()
    {
        assertThatThrownBy(() -> pebbleService.renderTemplate("Hello", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Template context must not be null");
    }
}
