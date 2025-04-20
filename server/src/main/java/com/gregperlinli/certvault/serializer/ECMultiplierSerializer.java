package com.gregperlinli.certvault.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.WNafL2RMultiplier;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * EC Multiplier Serializer
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code ECMultiplierSerializer}
 * @date 2025/4/20 18:57
 */
public class ECMultiplierSerializer extends JsonSerializer<ECMultiplier> {

    @Override
    public void serialize(ECMultiplier multiplier, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        // 序列化类名
        gen.writeStringField("class", multiplier.getClass().getName());

        // 尝试提取实现类中的 ECPoint 字段（假设存在）
        try {
            if (multiplier instanceof WNafL2RMultiplier obj) {
                Field field = obj.getClass().getDeclaredField("basePoint");
                field.setAccessible(true);
                ECPoint point = (ECPoint) field.get(obj);
                if (point != null) {
                    // 使用 ECPointSerializer 序列化该字段
                    provider.defaultSerializeValue(point, gen); // 自动使用已注册的 ECPointSerializer
                    gen.writeFieldName("basePoint");
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            gen.writeNullField("basePoint");
        }

        gen.writeEndObject();
    }

}
