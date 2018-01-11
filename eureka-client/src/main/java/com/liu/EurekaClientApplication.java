package com.liu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@MapperScan("com.liu.mapper")
@EnableJpaRepositories("com.liu.repository")
@EntityScan("com.liu.entity")
@SpringBootApplication(exclude=SolrAutoConfiguration.class)
@EnableCaching //启动缓存
@EnableDiscoveryClient
// @EnableScheduling //开启定时任务
public class EurekaClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientApplication.class, args);
	}
}
