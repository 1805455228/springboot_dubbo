package com.mydubbo.common.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 接口工具配置
 * @author zhuhelong
 * @since 2019年01月29日
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
    public Docket swaggerSpringMvcPlugin() {
		return new Docket(DocumentationType.SWAGGER_2).
				select().
				apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).
				build();
	}
}
