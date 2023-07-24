package com.atguigu.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping(value = "/login.html")
    public String login(@RequestParam(value = "originUrl") String originUrl , Model model) {
        model.addAttribute("originUrl" , originUrl) ;
        return "login" ;
    }

}
