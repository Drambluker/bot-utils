package org.vlaskin.bot.utils.callback;

/** Стабильный код события; коды уникальны внутри протокола и не переиспользуются. */
public interface CallbackEvent
{
    /** Возвращает непустой код из ASCII-букв, цифр, подчёркивания или дефиса. */
    String callbackCode();
}
