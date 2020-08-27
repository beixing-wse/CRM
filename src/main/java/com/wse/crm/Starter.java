package com.wse.crm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wse.crm.dao")//扫描包范围
public class Starter {

    public static void main(String[] args) {

        SpringApplication.run(com.wse.crm.Starter.class);

    }
}
