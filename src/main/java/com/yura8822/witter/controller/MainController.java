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
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
public class MainController {

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private UserRepo userRepo;

    @Value("${upload.patch}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting(){
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam (required = false, defaultValue = "") String filter, Map<String, Object> model) {
        List<Message> messages = messageRepo.findAll();
        if (filter != null && !filter.isEmpty()){
            messages = messageRepo.findByTag(filter);
        }else messages = messageRepo.findAll();
        model.put("messages", messages);
        model.put("filter", filter);
        return "main";
    }

    @PostMapping("/main")
    public String add(@AuthenticationPrincipal User user,
                      @Valid Message message,
                      BindingResult bindingResult,
                      Model model,
                      @RequestParam("file") MultipartFile file) throws IOException {

        message.setAuthor(user);

        if (bindingResult.hasErrors()){
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        }else {
            if(file != null && !file.getOriginalFilename().isEmpty()){
                File uploadDirect = new File(uploadPath);
                if(!uploadDirect.exists()){
                    uploadDirect.mkdir();
                }
                String uuodFile = UUID.randomUUID().toString();
                String resultFileName = uuodFile + "." + file.getOriginalFilename();
                file.transferTo(new File(uploadPath + "/" +resultFileName));
                message.setFilename(resultFileName);
            }
            model.addAttribute("message", null);
            messageRepo.save(message);
        }

        List<Message> messages = messageRepo.findAll();
        model.addAttribute("messages", messages);
        return "main";
    }

    @GetMapping("/user-messages/{userId}")
    public String userMessages(@AuthenticationPrincipal User currentUser,
                               @PathVariable String userId,
                               Model model){
        Long userIdLong = Long.parseLong(userId);
        User user = userRepo.findByUserID(userIdLong);
        Set<Message> messages = user.getMessages();

        model.addAttribute("messages", messages);
        model.addAttribute("checkedUser", user.equals(currentUser));

        return "userMessages";
    }

}
