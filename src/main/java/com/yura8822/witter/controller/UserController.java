package com.yura8822.witter.controller;

import com.yura8822.witter.domain.Role;
import com.yura8822.witter.domain.User;
import com.yura8822.witter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String getUsers(Map<String, Object> model){
        List<User> userList = userService.findAll();
        model.put("users", userList);
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping()
    public String saveUser(@RequestParam("userId") User user,
                           @RequestParam Map<String, Object> form,
                           @RequestParam String username){
        userService.saveUser(user, username, form);
        return "redirect:user";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public String userEditForm(@PathVariable(value = "id") Long id,
                               Map<String, Object> model){
        model.put("user", userService.findByUserID(id));
        model.put("roles", Role.values());
        return "userEdit";
    }

//    @GetMapping("{user}")
//    public String userEditForm(@PathVariable User user,
//                               Map<String, Object> model){
//        model.put("user", user);
//        model.put("roles", Role.values());
//        return "userEdit";
//    }

    @GetMapping("/profile")
    public String getProfile(Map<String, Object> model,
                             @AuthenticationPrincipal User user){
        model.put("username", user.getUsername());
        model.put("email", user.getEmail());
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal User user,
                                @RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String email) {
        userService.updateProfile(user, username, password, email);
        return "redirect:profile";
    }
}
