package org.vlaskin.bot.service;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Service
@AllArgsConstructor
public class PebbleService
{
    private final PebbleEngine pebbleEngine;

    public String renderTemplate(String templateName) throws IOException
    {
        StringWriter writer = new StringWriter();
        PebbleTemplate pebbleTemplate = pebbleEngine.getTemplate(templateName);
        pebbleTemplate.evaluate(writer);
        return writer.toString();
    }

    public String renderTemplate(String templateName, Map<String, Object> context) throws IOException
    {
        StringWriter writer = new StringWriter();
        PebbleTemplate pebbleTemplate = pebbleEngine.getTemplate(templateName);
        pebbleTemplate.evaluate(writer, context);
        return writer.toString();
    }
}
