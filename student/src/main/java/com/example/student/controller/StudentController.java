package com.example.student.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@Component
@RequestMapping("StudentApp")
public class StudentController {

    public static final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

    @GetMapping("/getStudentName/{id}")
    public ResponseEntity<String> getStudentNameById(@PathVariable("id") Integer id){
        HashMap<Integer, String> map = new HashMap();
        map.put(1,"SONALI");
        map.put(2,"RAM");
        map.put(3,"SHYAM");
        String name = map.get(id);
        LOGGER.info("Getting message :: " + name);
        return new ResponseEntity<>(name, HttpStatus.OK);
    }



}
