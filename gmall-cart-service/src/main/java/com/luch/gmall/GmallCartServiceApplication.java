package com.luch.gmall;

import com.luch.gmall.util.RedisUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.luch.gmall.cart.mapper")
public class GmallCartServiceApplication {


	public static void main(String[] args) {

		SpringApplication.run(GmallCartServiceApplication.class, args);
	}



}
