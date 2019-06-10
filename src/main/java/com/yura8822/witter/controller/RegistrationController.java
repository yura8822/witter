package com.yura8822.witter.controller;

import com.yura8822.witter.domain.User;
import com.yura8822.witter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model){

        if (!userService.addUser(user)){
            model.put("message", "User exists!");
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{uuid}")
    public String activate(Map<String, Object> model, @PathVariable String uuid){
        boolean isActivated = userService.activateUser(uuid);

        if (isActivated){
            model.put("message", "User seccessfully activated");
        } else {
            model.put("message", "Activation code not found");
        }

        return "login";
    }
}
