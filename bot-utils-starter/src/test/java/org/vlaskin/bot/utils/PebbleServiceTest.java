package org.vlaskin.bot.utils;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.LoaderException;
import io.pebbletemplates.pebble.error.ParserException;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.loader.StringLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
    void propagatesMissingTemplateError()
    {
        var service = new PebbleService(new PebbleEngine.Builder()
                .loader(new ClasspathLoader()).build());
        assertThatThrownBy(() -> service.renderTemplate("missing-bot-utils-template.peb"))
                .isInstanceOf(LoaderException.class);
    }

    @Test
    void propagatesInvalidTemplateSyntax()
    {
        assertThatThrownBy(() -> pebbleService.renderTemplate("{% if %}"))
                .isInstanceOf(ParserException.class);
    }

    @Test
    void parallelRenderingKeepsContextsIsolated() throws Exception
    {
        var executor = Executors.newFixedThreadPool(8, Thread.ofPlatform().daemon(true).factory());
        var start = new CountDownLatch(1);
        try
        {
            var futures = new ArrayList<Future<?>>();
            for (int i = 0; i < 8; i++)
            {
                String name = "Investor-" + i;
                futures.add(executor.submit(() -> {
                    start.await();
                    Map<String, String> context = Map.of("name", name);
                    for (int attempt = 0; attempt < 100; attempt++)
                    {
                        assertThat(pebbleService.renderTemplate("Hello, {{ name }}!", context))
                                .isEqualTo("Hello, " + name + "!");
                        assertThat(context).containsExactlyEntriesOf(Map.of("name", name));
                    }
                    return null;
                }));
            }
            start.countDown();
            for (var future : futures) future.get(5, TimeUnit.SECONDS);
        }
        finally
        {
            start.countDown();
            executor.shutdownNow();
            assertThat(executor.awaitTermination(2, TimeUnit.SECONDS)).isTrue();
        }
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
