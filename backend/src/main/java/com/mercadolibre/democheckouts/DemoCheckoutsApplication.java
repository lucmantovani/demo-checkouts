package com.mercadolibre.democheckouts;

import com.mercadopago.MercadoPago;
import com.mercadopago.exceptions.MPConfException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DemoCheckoutsApplication {

	public static void main(String[] args) throws MPConfException {
		MercadoPago.SDK.setAccessToken("TEST-130106526144588-071514-fd1650292768bb6c10b392e34dc20dbd-791638501");
		SpringApplication.run(DemoCheckoutsApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**");
			}
		};
	}

}
