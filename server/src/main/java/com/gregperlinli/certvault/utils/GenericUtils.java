package com.gregperlinli.certvault.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 通用工具类
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className GenericUtils
 * @date 2024/1/31 14:47
 */
public class GenericUtils {
    /**
     * 标准时间模式串
     */
    private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss SSS";

    /**
     * 生成时间字符串，精度到秒
     */
    public static String getFullTimeStr(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String s = sdf.format(date);
        return s;
    }


    /**
     * 按照模式串生成时间字符串，模式自定义
     * @param pattern 时间模式串，标准格式：yyyy-MM-dd HH:mm:ss SSS，可以自定义生成精度，比如yyyy-MM-dd就是年月日
     * @return String
     */
    public static String getFullTimeStr(String pattern){
        SimpleDateFormat sdf = null;
        Date date = new Date();
        if(!ofNullable(pattern)||!TIME_PATTERN.contains(pattern)){
            sdf = new SimpleDateFormat(TIME_PATTERN);
            return sdf.format(date);
        }
        sdf = new SimpleDateFormat(pattern);
        String s = sdf.format(date);
        return s;
    }

    /**
     * 判断多个参数是否为null，如果是字符串，则长度为0也算空
     * @param args ...
     * @return boolean
     */
    public static boolean allOfNullable(Object... args){
        for (Object a:args){
            if(a instanceof String){
                if(a==null||((String) a).length() == 0){
                    return false;
                }
            }else{
                if(a==null){
                    return false;
                }
            }
        }
        return true;
    }


    public static boolean allOfNullable(Object obj) throws IllegalAccessException {
        if(obj == null){
            return false;
        }
        Class c = obj.getClass();
        Field[] fields = c.getDeclaredFields();
        for(Field field : fields) {
            field.setAccessible(true);
            if(field.get(obj) == null){
                return false;
            }
        }
        return true;
    }


    /**
     * 判断一个参数是否是null
     */
    public static boolean ofNullable(Object o){
        return o != null;
    }


    /**
     * 简单拼接url，一般用于get请求的url拼接参数
     * @param sourceUrl 不带参数的url
     * @param params 键值对形式的参数，格式为 参数名,值
     * @return String
     */
    public static String splicingUrlStr(String sourceUrl, Map<String,String> params){
        if(!allOfNullable(sourceUrl,params)){
            return sourceUrl;
        }
        StringBuilder url = new StringBuilder(sourceUrl);
        url.append("?");
        Set<String> keys = params.keySet();
        for (String key : keys){
            if(!ofNullable(key)&&ofNullable(params.get(key))){
                return sourceUrl;
            }
            url.append(key).append("=").append(params.get(key)).append("&");
        }
        url.deleteCharAt(url.length()-1);
        return url.toString();
    }

    /**
     * 将实体类转换为Map
     *
     * @param object 需要转换的对象
     * @return 对象的 Map
     * @throws IllegalAccessException 非法访问异常
     */
    public static Map<String, Object> entityToMap(Object object) throws IllegalAccessException {
        if ( object == null ) {
            return null;
        }
        Map<String, Object> map = new HashMap<>(10);

        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(object));
        }
        return map;
    }

    /**
     *  将Map转换为实体类
     *
     * @param map 需要转换的Map
     * @param entity 转换的实体类
     * @return 转换后的实体类
     * @param <T> 泛型
     * @throws NoSuchMethodException 方法不存在异常
     * @throws IllegalAccessException 非法访问异常
     * @throws InvocationTargetException 调用目标异常
     * @throws InstantiationException 实例化异常
     */
    public static <T> T mapToEntity(Map<String, Object> map, Class<T> entity) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null == map){
            return null;
        }
        T t = entity.getDeclaredConstructor().newInstance();
        for(Field field : entity.getDeclaredFields()) {
            if (map.containsKey(field.getName())) {
                boolean flag = field.isAccessible();
                field.setAccessible(true);
                Object object = map.get(field.getName());
                if (object!= null && field.getType().isAssignableFrom(object.getClass())) {
                    field.set(t, object);
                }
                field.setAccessible(flag);
            }
        }
        return t;
    }
}
