package org.vlaskin.bot.utils.callback;

import java.util.List;
import java.util.UUID;

/**
 * Неизменяемый список параметров; индексы отсчитываются с нуля.
 * Null-список и null-элементы запрещены. Ошибки количества, индексов и типов
 * приводят к IllegalArgumentException; строковые значения не обрезаются.
 */
public final class CallbackParameters
{
    private final List<String> values;

    public CallbackParameters(List<String> values)
    {
        if (values == null || values.stream().anyMatch(value -> value == null))
            throw new IllegalArgumentException("Callback parameters are missing");
        this.values = List.copyOf(values);
    }

    /** Возвращает неизменяемую копию исходных значений. */
    public List<String> values() { return values; }

    /** Требует точное неотрицательное количество параметров. */
    public CallbackParameters requireSize(int expected)
    {
        if (expected < 0 || values.size() != expected)
            throw new IllegalArgumentException("Unexpected callback parameter count");
        return this;
    }

    /** Требует как минимум указанное неотрицательное количество параметров. */
    public CallbackParameters requireAtLeast(int minimum)
    {
        if (minimum < 0 || values.size() < minimum)
            throw new IllegalArgumentException("Not enough callback parameters");
        return this;
    }

    /** Читает непустое значение; name используется только для диагностики. */
    public String string(int index, String name)
    {
        if (index < 0 || index >= values.size())
            throw new IllegalArgumentException("Callback parameter " + name + " is missing");
        String value = values.get(index);
        if (value.isBlank())
            throw new IllegalArgumentException("Callback parameter " + name + " is blank");
        return value;
    }

    /** Читает знаковое десятичное число в диапазоне int, без пробелов по краям. */
    public int integer(int index, String name)
    {
        try { return Integer.parseInt(string(index, name)); }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Callback parameter " + name + " is not an integer", e);
        }
    }

    /** Читает строго положительное число, например номер страницы. */
    public int positiveInteger(int index, String name)
    {
        int value = integer(index, name);
        if (value <= 0)
            throw new IllegalArgumentException("Callback parameter " + name + " must be positive");
        return value;
    }

    /** Читает канонический компактный UUID, а не стандартное представление с дефисами. */
    public UUID uuid(int index, String name)
    {
        return CompactUuid.decode(string(index, name));
    }

    /** Возвращает неизменяемый хвост; индекс, равный размеру, даёт пустой список. */
    public List<String> tail(int fromIndex)
    {
        if (fromIndex < 0 || fromIndex > values.size())
            throw new IllegalArgumentException("Callback parameter tail is out of bounds");
        return values.subList(fromIndex, values.size());
    }
}
