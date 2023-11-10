package com.h3c.ywrj.dzkf.hkcustoms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupIp;
import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupPort;

import java.util.Date;

/**
 * Created by Jeff on 2019-12-19 10:51
 */
public class JsonIgnoreTest {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        testIp(mapper);
        testPort(mapper);
    }

    private static void testIp(ObjectMapper mapper) {
        BackupIp ip = new BackupIp("1.1.1.1");
        ip.setCreatedTime(new Date());
        ip.setScannedCount(4);
        System.out.println(ip.getCreatedTime());
        try {
            System.out.println(mapper.writeValueAsString(ip));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private static void testPort(ObjectMapper mapper) {
        BackupPort port = new BackupPort("1.1.1.1", 80, "tcp", "open", "ss");
        port.setId(200);
        port.setCreatedTime(new Date());
        System.out.println(port.getCreatedTime());
        try {
            System.out.println(mapper.writeValueAsString(port));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
