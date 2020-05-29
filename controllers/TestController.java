package com.codeup.springblogapp.controllers;
import com.codeup.springblogapp.model.Book;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

    @Controller
public class TestController {

        @GetMapping("/test")
        @ResponseBody
        public void hello() {
        String name = "Mike";
            System.out.println("Hello From Spring");        }
    }
