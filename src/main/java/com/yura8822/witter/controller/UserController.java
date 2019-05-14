package com.yura8822.witter.controller;

import com.yura8822.witter.domain.Role;
import com.yura8822.witter.domain.User;
import com.yura8822.witter.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public String getUsers(Map<String, Object> model){
        List<User> userList = userRepo.findAll();
        model.put("users", userList);
        return "userList";
    }

    @PostMapping()
    public String saveUser(@RequestParam("userId") User user,
                           @RequestParam Map<String, Object> form,
                           @RequestParam String username){
        user.setUsername(username);
        user.getRoles().clear();
        Set<String> roles = Arrays.stream(Role.values()).map(Role::getAuthority)
                .collect(Collectors.toSet());

        for (String key : form.keySet()){
            if (roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepo.save(user);
        return "redirect:user";
    }

    @GetMapping("/{id}")
    public String userEditForm(@PathVariable(value = "id") Long id,
                               Map<String, Object> model){
        model.put("user", userRepo.findByUserID(id));
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


}
