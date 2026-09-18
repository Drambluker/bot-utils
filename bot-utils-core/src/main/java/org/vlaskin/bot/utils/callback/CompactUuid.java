package org.vlaskin.bot.utils.callback;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;
import java.util.Objects;

/** Обратимое URL-safe представление UUID длиной 22 символа, без padding. */
public final class CompactUuid
{
    private CompactUuid() {}

    /** Кодирует ненулевой UUID; null приводит к NullPointerException. */
    public static String encode(UUID id)
    {
        Objects.requireNonNull(id, "UUID must not be null");
        ByteBuffer bytes = ByteBuffer.allocate(16);
        bytes.putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes.array());
    }

    /** Принимает только канонические 22 символа; ошибочный ввод приводит к IllegalArgumentException. */
    public static UUID decode(String value)
    {
        if (value == null || !value.matches("[A-Za-z0-9_-]{22}"))
            throw new IllegalArgumentException("Invalid compact UUID");
        byte[] bytes = Base64.getUrlDecoder().decode(value);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        UUID id = new UUID(buffer.getLong(), buffer.getLong());
        if (!encode(id).equals(value))
            throw new IllegalArgumentException("Non-canonical compact UUID");
        return id;
    }
}
