package com.gregperlinli.certvault.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTimeToOffsetSerializer
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code LocalDateTimeToOffsetSerializer}
 * @date 2025/3/24 12:44
 */
public class LocalDateTimeToOffsetSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // 将 LocalDateTime 转换为 OffsetDateTime（假设时区为系统默认）
        OffsetDateTime offsetDateTime = value.atZone(ZoneId.systemDefault()).toOffsetDateTime();
        generator.writeString(offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
}
