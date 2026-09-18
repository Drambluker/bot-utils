package org.vlaskin.bot.utils.callback;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CallbackCodecTest
{
    @Test
    void handlesLongWhitespaceRunsInLinearTime()
    {
        var payload = new CallbackPayload("event", List.of("x" + " ".repeat(100_000) + "x"));
        org.junit.jupiter.api.Assertions.assertTimeout(java.time.Duration.ofSeconds(2),
                () -> assertThat(CallbackCodec.decode(CallbackCodec.encode(payload))).isEqualTo(payload));
        assertThatThrownBy(() -> CallbackCodec.encode(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void roundTripsImmutablePayloadWithoutImposingPlatformLimits()
    {
        var payload = new CallbackPayload("buy", List.of("x".repeat(100), "русский"));
        assertThat(CallbackCodec.decode(CallbackCodec.encode(payload))).isEqualTo(payload);
        assertThat(CallbackCodec.encode(new CallbackPayload("main", List.of()))).isEqualTo("main");
        assertThatThrownBy(() -> payload.parameters().add("x")).isInstanceOf(UnsupportedOperationException.class);
        assertThat(CallbackCodec.decode("buy, 1, 2").parameters()).containsExactly("1", "2");
    }

    @Test
    void rejectsAmbiguousOrMissingData()
    {
        for (String value : Arrays.asList(null, "", " ", "buy,", "buy,,1", "buy, ", "?,1"))
            assertThatThrownBy(() -> CallbackCodec.decode(value)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CallbackPayload(null, List.of())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CallbackPayload("x", null)).isInstanceOf(IllegalArgumentException.class);
        for (String value : Arrays.asList(null, "", " ", "a,b", " x"))
            assertThatThrownBy(() -> new CallbackPayload("x", Arrays.asList(value)))
                    .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void readsTypedParametersAndRejectsInvalidValues()
    {
        UUID id = UUID.randomUUID();
        var params = new CallbackParameters(List.of(CompactUuid.encode(id), "10", "-1"));
        assertThat(params.requireSize(3).requireAtLeast(1)).isSameAs(params);
        assertThat(params.uuid(0, "id")).isEqualTo(id);
        assertThat(params.positiveInteger(1, "page")).isEqualTo(10);
        assertThat(params.integer(2, "signed")).isEqualTo(-1);
        assertThat(params.tail(1)).containsExactly("10", "-1");
        for (int index : new int[]{-1, 3})
            assertThatThrownBy(() -> params.string(index, "id")).isInstanceOf(IllegalArgumentException.class);
        for (int count : new int[]{-1, 4})
        {
            assertThatThrownBy(() -> params.requireSize(count)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> params.requireAtLeast(count)).isInstanceOf(IllegalArgumentException.class);
        }
        for (int index : new int[]{-1, 4})
            assertThatThrownBy(() -> params.tail(index)).isInstanceOf(IllegalArgumentException.class);
        for (String value : List.of("", " ", "x", "0", "-1", "2147483648"))
            assertThatThrownBy(() -> new CallbackParameters(List.of(value)).positiveInteger(0, "page"))
                    .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CallbackParameters(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CallbackParameters(Arrays.asList((String) null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void roundTripsCanonicalCompactUuidAndRejectsMalformedEncodings()
    {
        UUID id = UUID.randomUUID();
        String encoded = CompactUuid.encode(id);
        assertThat(encoded).hasSize(22);
        assertThatThrownBy(() -> CompactUuid.encode(null)).isInstanceOf(NullPointerException.class);
        assertThat(CompactUuid.decode(encoded)).isEqualTo(id);
        for (String value : Arrays.asList(null, "bad", "!".repeat(22), encoded + "==", "A".repeat(21) + "B"))
            assertThatThrownBy(() -> CompactUuid.decode(value)).isInstanceOf(IllegalArgumentException.class);
    }
}
