package com.gregperlinli.certvault.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Big Integer to String Serializer
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code BigIntegerAsStringSerializer}
 * @date 2025/3/25 16:43
 */
public class BigIntegerToStringSerializer extends JsonSerializer<BigInteger> {

    @Override
    public void serialize(BigInteger value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(value == null ? null : value.toString());
    }
}
