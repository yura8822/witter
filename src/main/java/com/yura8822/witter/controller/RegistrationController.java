package com.yura8822.witter.controller;

import com.yura8822.witter.domain.User;
import com.yura8822.witter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.HashMap;
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
    public String addUser(@Valid User user, BindingResult bindingResult, Model model){

        if (!StringUtils.isEmpty(user.getPassword())
                && !StringUtils.isEmpty(user.getPasswordConfirmation())
                && !user.getPassword().equals(user.getPasswordConfirmation())){
            Map<String,String> errorsPasword = new HashMap<>();
            errorsPasword.put("passwordError", "Passwords do not match");
            errorsPasword.put("passwordConfirmationError", "Passwords do not match");
            model.mergeAttributes(errorsPasword);
        }

        if (bindingResult.hasErrors() || model.containsAttribute("passwordError") || model.containsAttribute("passwordConfirmationError")){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }

        if (!userService.addUser(user)){
            model.addAttribute("userName", "Username exists!");
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
