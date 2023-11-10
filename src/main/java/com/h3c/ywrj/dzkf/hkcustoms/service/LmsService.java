package com.h3c.ywrj.dzkf.hkcustoms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by @author wfw2525 on 2019/12/10 14:46
 */
@Component
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
public class LmsService {
    @Autowired
    private MessageSource ms;

    public String getMessage(String msgCode) {
        return getMessage(msgCode, null);
    }

    public String getMessage(String msgCode, Object[] args) {
        return getMessage(msgCode, args, "");
    }

    public String getMessage(String msgCode, Object[] args, String defaultMessage) {
        return ms.getMessage(msgCode, args, defaultMessage, LocaleContextHolder.getLocale());
    }
}
