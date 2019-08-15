package com.yura8822.witter.controller;

import com.yura8822.witter.domain.Message;
import com.yura8822.witter.domain.User;
import com.yura8822.witter.repo.MessageRepo;
import com.yura8822.witter.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;

@Controller
public class MessageController {

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private UserRepo userRepo;

    @Value("${upload.patch}")
    private String uploadPath;

    @GetMapping("/user-messages/{userId}")
    public String userMessages(@AuthenticationPrincipal User currentUser,
                               @PathVariable String userId,
                               Model model,
                               @RequestParam(required = false) String messageId){
        User user = userRepo.findByUserID(Long.parseLong(userId));
        Set<Message> messages = user.getMessages();

        if (messageId != null){
            model.addAttribute("message", messageRepo.findByMessageId(Integer.parseInt(messageId)));
        }

        model.addAttribute("userChannel", user);
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
        model.addAttribute("messages", messages);
        model.addAttribute("checkedUser", user.equals(currentUser) && messageId != null);
        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());

        return "userMessages";
    }

    @PostMapping("/user-messages/{userId}")
    public String edit(@AuthenticationPrincipal User currentUser,
                       @Valid Message message,
                       BindingResult bindingResult,
                       Model model,
                       @RequestParam("file") MultipartFile file,
                       @PathVariable String userId,
                       @RequestParam(required = false) String messageId) throws IOException {

        User user = userRepo.findByUserID(Long.parseLong(userId));
        Message byMessageId = null;

        if (messageId != null) {
            byMessageId = messageRepo.findByMessageId(Integer.parseInt(messageId));
        }
        message.setFilename(byMessageId.getFilename());
        message.setAuthor(currentUser);

        ControllerUtils.saveMessage(bindingResult, message, model, file, uploadPath, messageRepo);

        model.addAttribute("messages", user.getMessages());
        model.addAttribute("checkedUser", user.equals(currentUser)
                && messageId != null && bindingResult.hasErrors());

        return "userMessages";
    }
}
