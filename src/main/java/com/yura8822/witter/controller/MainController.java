package com.yura8822.witter.controller;

import com.yura8822.witter.domain.Message;
import com.yura8822.witter.domain.User;
import com.yura8822.witter.repo.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class MainController {

    @Autowired
    private MessageRepo messageRepo;

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
                      @RequestParam String text,
                      @RequestParam String tag,
                      @RequestParam("file") MultipartFile file,
                      Map<String, Object> model) throws IOException {
        Message message = new Message(text, tag, user);
        if(file != null && !file.getOriginalFilename().isEmpty()){
            File uploadDirect = new File(uploadPath);
            if(uploadDirect.exists()){
                uploadDirect.mkdir();
            }
            String uuodFile = UUID.randomUUID().toString();
            String resultFileName = uuodFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" +resultFileName));
            message.setFilename(resultFileName);
        }
        messageRepo.save(message);
        List<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "main";
    }
}
