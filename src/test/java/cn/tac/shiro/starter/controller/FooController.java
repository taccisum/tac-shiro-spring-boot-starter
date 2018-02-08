package cn.tac.shiro.starter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tac
 * @since 1.0
 */
@RestController
@RequestMapping("foo")
public class FooController {
    @GetMapping("index")
    public String index() {
        return "index";
    }
}
