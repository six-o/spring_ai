package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
				// 要攔截的路徑
				.addMapping("/**")
				// 允許來自這個來源的請求
				.allowedOrigins("http://localhost:5173", "https://cw8qzpxv-8080.asse.devtunnels.ms") // 允許的前端來源
				// 允許的方法（可根據需求增減）
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				// 允許的 Header（* 代表全部）
				.allowedHeaders("*")
				// 允許攜帶憑證（cookie）
				.allowCredentials(true);
	}
}
