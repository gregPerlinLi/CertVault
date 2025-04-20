package com.gregperlinli.certvault.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;

/**
 * EC Point Serializer
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code ECPointSerializer}
 * @date 2025/4/20 18:50
 */
public class ECPointSerializer extends JsonSerializer<ECPoint> {

    @Override
    public void serialize(ECPoint ecPoint, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        if (ecPoint.isInfinity()) {
            // 处理无穷远点
            gen.writeStringField("type", "infinity");
        } else {
            // 标准化坐标到仿射形式
            ECPoint normalizedPoint = ecPoint.normalize();
            ECFieldElement x = normalizedPoint.getAffineXCoord();
            ECFieldElement y = normalizedPoint.getAffineYCoord();

            // 将 ECFieldElement 转换为 BigInteger（假设支持 toBigInteger() 方法）
            String xValue = x.toBigInteger().toString(); // 需确认 ECFieldElement 是否有此方法
            String yValue = y.toBigInteger().toString();

            gen.writeStringField("x", xValue);
            gen.writeStringField("y", yValue);

            // 使用反射调用 protected 方法
            try {
                int coordinateSystem = (int) getCurveCoordinateSystemMethod.invoke(normalizedPoint);
                gen.writeNumberField("coordinateSystem", coordinateSystem);
            } catch (Exception e) {
                gen.writeNullField("coordinateSystem");
            }

        }

        gen.writeEndObject();
    }

    // 缓存反射获取的 Method 对象（避免重复反射开销）
    private Method getCurveCoordinateSystemMethod;

    public ECPointSerializer() {
        try {
            // 获取 protected 方法
            getCurveCoordinateSystemMethod = ECPoint.class.getDeclaredMethod("getCurveCoordinateSystem");
            getCurveCoordinateSystemMethod.setAccessible(true); // 设置可访问
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to access getCurveCoordinateSystem()", e);
        }
    }

}
