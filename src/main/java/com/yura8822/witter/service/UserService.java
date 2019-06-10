package com.yura8822.witter.service;

import com.yura8822.witter.domain.Role;
import com.yura8822.witter.domain.User;
import com.yura8822.witter.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailSender mailSende;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    public boolean addUser(User user){
        User userFromDB = userRepo.findByUsername(user.getUsername());

        if (userFromDB != null){
        return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        userRepo.save(user);

        if (!StringUtils.isEmpty(user.getEmail())){
            String message = String.format("Hello %s \n" +
                    "Welcome. Please visit link: http://localhost:8080/activate/%s",
                    user.getUsername(), user.getActivationCode());

            mailSende.sendMail(user.getEmail(), "Activation code", message);
        }
        return true;
    }

    public boolean activateUser(String uuid){
        User user = userRepo.findByUserActivateCode(uuid);

        if (user == null)
            return false;

        user.setActivationCode(null);
        userRepo.save(user);
        return true;
    }
}
