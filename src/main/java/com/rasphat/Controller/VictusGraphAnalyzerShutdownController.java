package com.rasphat.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VictusGraphAnalyzerShutdownController {
    private final ApplicationContext context;

    @Autowired
    public VictusGraphAnalyzerShutdownController(ApplicationContext context) {
        this.context = context;
    }

    @GetMapping("/shutdown")
    public int shutdown() {
        return SpringApplication.exit(context, () -> 0);
    }
}