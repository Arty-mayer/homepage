package com.homepage.Controller;

import com.homepage.service.MenuHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu")
public class MenuAPIController {
    MenuHandler menuHandler;
    public MenuAPIController(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> menuAlsJSON(@PathVariable Long id) {
        return new ResponseEntity<>("{\"id\":" + id + "}", HttpStatus.OK);
    }

}
