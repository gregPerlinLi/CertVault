package com.gregperlinli.certvault.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.gregperlinli.certvault.serializer.BigIntegerToStringSerializer;
import com.gregperlinli.certvault.serializer.ECMultiplierSerializer;
import com.gregperlinli.certvault.serializer.ECPointSerializer;
import com.gregperlinli.certvault.serializer.LocalDateTimeToOffsetSerializer;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson JSON Serializer Configuration
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code JacksonConfig}
 * @date 2025/3/24 12:33
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeToOffsetSerializer());
        module.addSerializer(BigInteger.class, new BigIntegerToStringSerializer());
        module.addSerializer(ECPoint.class, new ECPointSerializer());
        module.addSerializer(ECMultiplier.class, new ECMultiplierSerializer());
        module.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        mapper.registerModule(module);
        return mapper;
    }

}
