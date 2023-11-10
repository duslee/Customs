package com.h3c.ywrj.dzkf.hkcustoms.config;

import com.h3c.ywrj.dzkf.hkcustoms.common.util.DateUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * Created by @author Jeff on 2019-12-13 09:38
 */
@Configuration
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
public class AppConfig {
    @Bean(value = "s1")
    public SimpleDateFormat sdf() {
        return DateUtils.SDF1;
    }

    @Bean(value = "s2")
    public SimpleDateFormat sdf2() {
        return DateUtils.SDF2;
    }
}
