package com.example.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@FeignClient(value = "DepartmentApp", url = "http://localhost:8082/DepartmentApp")
public interface DepartmentFeignClient {

        @GetMapping("/getStudentMailId/{id}")
        String getStudentMailById(@PathVariable("id") String id);
}
