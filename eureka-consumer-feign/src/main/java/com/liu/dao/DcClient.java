package com.liu.dao;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("api-service")
public interface DcClient {

    @GetMapping("/user/find/2")
    String consumer();

}
