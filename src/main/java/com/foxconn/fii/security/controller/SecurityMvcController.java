package com.foxconn.fii.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.config.CustomReloadableResourceBundleMessageSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Slf4j
@Controller
public class SecurityMvcController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomReloadableResourceBundleMessageSource messageSource;

    @RequestMapping("/sign-in")
    public String signIn(HttpServletRequest request, Model model, Locale locale) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || "anonymousUser".equals(authentication.getName())) {
            model.addAttribute("path", "sign-in");
            try {
                model.addAttribute("messages", objectMapper.writeValueAsString(messageSource.getAllProperties(locale)));
            } catch (Exception e) {
                model.addAttribute("messages", "{}");
            }

            return "login";
        } else {
            return "redirect:/home";
        }
    }
}
