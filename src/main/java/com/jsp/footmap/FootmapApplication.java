package com.jsp.footmap;

import com.jsp.footmap.controller.ThreadController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;


@SpringBootApplication
public class FootmapApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootmapApplication.class, args);
	}

	/**
	 * 配置上传文件大小
	 * @return
	 */
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		//单个数据大小
		factory.setMaxFileSize("1024KB");
		factory.setMaxRequestSize("1024KB");
		return factory.createMultipartConfig();
	}
}
