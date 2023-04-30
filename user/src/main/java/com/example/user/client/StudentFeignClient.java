package com.example.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(value = "StudentApp", url = "http://localhost:8081/StudentApp")
public interface StudentFeignClient {
	
	@GetMapping("/getStudentName/{id}")
	String getStudentNameById(@PathVariable("id") String id);
	
}
