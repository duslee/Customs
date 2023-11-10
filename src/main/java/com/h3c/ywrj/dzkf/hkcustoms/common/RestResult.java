package com.h3c.ywrj.dzkf.hkcustoms.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST响应的统一封装
 * <p>
 * Created by @author wfw2525 on 2019/12/10 17:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResult<T> {
    /**
     * 状态值：0，成功；其它，失败
     */
    private int code;
    /**
     * 描述信息
     */
    private String msg;
    /**
     * 封装数据
     */
    private T data;
}
