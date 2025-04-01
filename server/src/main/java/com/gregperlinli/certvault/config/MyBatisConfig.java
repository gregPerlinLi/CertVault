package com.gregperlinli.certvault.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis Data Config
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code MyBatisConfig}
 * @date 2025/3/3 15:58
 */
@MapperScan("com.gregperlinli.certvault.mapper")
@Configuration
@Slf4j
public class MyBatisConfig {

    @Value(value = "${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        DbType dbType = null;
        if ("org.postgresql.Driver".equals(driverClassName)) {
            dbType = DbType.POSTGRE_SQL;
        } else {
            dbType = DbType.MYSQL;
        }
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType)); // 如果配置多个插件, 切记分页最后添加
        // 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
        return interceptor;
    }

    @Bean
    public MybatisPlusPropertiesCustomizer mybatisPlusPropertiesCustomizer() {
        return properties -> {
            GlobalConfig globalConfig = properties.getGlobalConfig();
            GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
            // 设置字段和表名双引号包裹
            if ("org.postgresql.Driver".equals(driverClassName)) {
                dbConfig.setColumnFormat("\"%s\"");
                dbConfig.setTableFormat("\"%s\"");
            } else {
                dbConfig.setColumnFormat("`%s`");
                dbConfig.setTableFormat("`%s`");
            }
        };
    }

}