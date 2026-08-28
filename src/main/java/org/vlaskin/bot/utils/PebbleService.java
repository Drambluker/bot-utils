package org.vlaskin.bot.utils;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Сервис загрузки и рендеринга шаблонов сообщений через настроенный {@link PebbleEngine}. */
public final class PebbleService
{
    private final PebbleEngine pebbleEngine;

    /**
     * Создаёт сервис с заданным движком шаблонов.
     *
     * @param pebbleEngine настроенный движок Pebble
     */
    public PebbleService(PebbleEngine pebbleEngine)
    {
        this.pebbleEngine = Objects.requireNonNull(pebbleEngine,
                "Pebble engine must not be null");
    }

    /**
     * Загружает шаблон по имени и рендерит его без контекста.
     *
     * @param templateName имя шаблона для настроенного загрузчика Pebble
     * @return отрендеренный текст
     * @throws IOException если шаблон не удалось прочитать или записать
     */
    public String renderTemplate(String templateName) throws IOException
    {
        StringWriter writer = new StringWriter();
        PebbleTemplate pebbleTemplate = pebbleEngine.getTemplate(requireTemplateName(templateName));
        pebbleTemplate.evaluate(writer);
        return writer.toString();
    }

    /**
     * Загружает шаблон по имени и рендерит его с контекстом.
     *
     * @param templateName имя шаблона для настроенного загрузчика Pebble
     * @param context значения, доступные выражениям шаблона
     * @return отрендеренный текст
     * @throws IOException если шаблон не удалось прочитать или записать
     */
    public String renderTemplate(String templateName, Map<String, ?> context) throws IOException
    {
        StringWriter writer = new StringWriter();
        PebbleTemplate pebbleTemplate = pebbleEngine.getTemplate(requireTemplateName(templateName));
        Map<String, Object> templateContext = new HashMap<>(Objects.requireNonNull(context,
                "Template context must not be null"));
        pebbleTemplate.evaluate(writer, templateContext);
        return writer.toString();
    }

    private static String requireTemplateName(String templateName)
    {
        Objects.requireNonNull(templateName, "Template name must not be null");
        if (templateName.isBlank())
            throw new IllegalArgumentException("Template name must not be blank");
        return templateName;
    }
}
