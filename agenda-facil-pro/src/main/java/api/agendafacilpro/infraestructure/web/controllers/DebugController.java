package api.agendafacilpro.infraestructure.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/thread")
    public String getThreadName() {
        return "Thread: " + Thread.currentThread().getName();
    }
}
