package com.homepage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String showHome() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String showIndex() {
        return "index";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }
    
    @GetMapping("/signup")
    public String showSignup() {
        return "signup";
    }
    
    @GetMapping("/kilometer")
    public String showKilometerPage() {
        return "nutzwagenkilometer_linie";
    }
    
    @GetMapping("/users/management")
    public String showUserManagement() {
        return "user-management";
    }
    
    @GetMapping("/content/management")
    public String showContentManagement() {
        return "content-management";
    }
}
