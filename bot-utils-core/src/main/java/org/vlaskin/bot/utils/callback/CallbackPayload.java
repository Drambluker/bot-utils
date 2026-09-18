package org.vlaskin.bot.utils.callback;

import java.util.List;

/**
 * Данные кнопки с неизменяемыми параметрами. Код состоит из ASCII-букв, цифр, _ и -.
 * Null, пустые параметры, запятые и пробелы по краям запрещены.
 * Уникальность кодов, авторизацию и ограничения размера проверяет приложение.
 */
public record CallbackPayload(String eventCode, List<String> parameters)
{
    public CallbackPayload
    {
        if (eventCode == null || !eventCode.matches("[A-Za-z0-9_-]+"))
            throw new IllegalArgumentException("Invalid callback event code");
        if (parameters == null || parameters.stream().anyMatch(
                value -> value == null || value.isBlank() || !value.equals(value.strip()) || value.contains(",")))
            throw new IllegalArgumentException("Invalid callback parameters");
        parameters = List.copyOf(parameters);
    }
}
