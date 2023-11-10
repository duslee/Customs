package com.h3c.ywrj.dzkf.hkcustoms.controller;

import com.h3c.ywrj.dzkf.hkcustoms.service.LmsService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by @author wfw2525 on 2019/12/10 14:51
 */
@RestController
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
@Api(tags = "用于测试，可忽略")
public class HelloController {
    @Autowired
    private LmsService ls;

    @GetMapping("/hello")
    public String hello() {
        return ls.getMessage("hello.world");
    }
}
