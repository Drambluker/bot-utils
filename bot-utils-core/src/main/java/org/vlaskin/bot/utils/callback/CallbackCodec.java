package org.vlaskin.bot.utils.callback;

import java.util.List;
import java.util.Objects;
import java.util.Arrays;

/** Компактный формат с разделителем; ограничения размера проверяет адаптер платформы. */
public final class CallbackCodec
{
    private CallbackCodec() {}

    /** Кодирует данные; null запрещён, ограничение длины задаёт платформа. */
    public static String encode(CallbackPayload payload)
    {
        Objects.requireNonNull(payload, "Callback payload must not be null");
        return payload.parameters().isEmpty() ? payload.eventCode()
                : payload.eventCode() + "," + String.join(",", payload.parameters());
    }

    /** Разбирает данные за линейное время, удаляя пробелы вокруг разделителей. */
    public static CallbackPayload decode(String data)
    {
        if (data == null || data.isBlank())
            throw new IllegalArgumentException("Callback data is blank");
        List<String> fields = Arrays.stream(data.split(",", -1)).map(String::strip).toList();
        return new CallbackPayload(fields.getFirst(), fields.subList(1, fields.size()));
    }
}
