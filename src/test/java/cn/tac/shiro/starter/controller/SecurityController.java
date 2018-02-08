package cn.tac.shiro.starter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author tac
 * @since 1.0
 */
@Controller
@RequestMapping("security")
public class SecurityController {
    @GetMapping("login")
    public String login() {
        return "/login";
    }
}
