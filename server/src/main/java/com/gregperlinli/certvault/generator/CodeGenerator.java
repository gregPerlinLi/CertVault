package com.gregperlinli.certvault.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

/**
 * MyBatis Plus Generator
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CodeGenerator}
 * @date 2025/3/3 19:29
 */
public class CodeGenerator {

    public static void main(String[] args) {
        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/cert_vault?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", "cert_vault", "cert_vault")
                .globalConfig(builder -> {
                    builder.author("gregPerlinLi") // 设置作者
                            .outputDir("server/src/main/java"); // 输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.gregperlinli.certvault") // 设置父包名
                            .entity("domain.entities") // 设置实体类包名
                            .mapper("mapper") // 设置 Mapper 接口包名
                            .service("service.interfaces") // 设置 Service 接口包名
                            .serviceImpl("service.impl") // 设置 Service 实现类包名
                            .xml("mapper"); // 设置 Mapper XML 文件包名
                })
                .strategyConfig(builder -> {
                    builder.addInclude("certificate") // 设置需要生成的表名
                            .entityBuilder()
                            .enableLombok() // 启用 Lombok
                            .enableTableFieldAnnotation() // 启用字段注解
                            .controllerBuilder()
                            .enableRestStyle(); // 启用 REST 风格
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute(); // 执行生成
    }

}
