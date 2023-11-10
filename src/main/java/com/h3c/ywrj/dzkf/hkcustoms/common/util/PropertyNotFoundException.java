package com.h3c.ywrj.dzkf.hkcustoms.common.util;

/**
 * Created by @author wfw2525 on 2019/12/23 23:49
 */
public class PropertyNotFoundException extends RuntimeException {
    public PropertyNotFoundException() {
        super();
    }

    public PropertyNotFoundException(String msg) {
        super(msg);
    }

    public PropertyNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
