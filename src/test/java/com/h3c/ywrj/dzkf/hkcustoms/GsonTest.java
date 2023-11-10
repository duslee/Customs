package com.h3c.ywrj.dzkf.hkcustoms;

import com.h3c.ywrj.dzkf.hkcustoms.common.util.GsonUtils;
import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupPort;

/**
 * Created by wfw2525 on 2019/12/25 16:28
 */
public class GsonTest {
    public static void main(String[] args) {
        test();
    }

    private static void test() {
        String jsonData = "[{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":135,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"msrpc\"},{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":139,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"netbios-ssn\"},{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":445,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"microsoft-ds\"},{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":1025,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"NFS-or-IIS\"},{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":1026,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"LSA-or-nterm\"},{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":1027,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"IIS\"},{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":1036,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"nsstp\"},{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":1046,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"wfremotertm\"},{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":1047,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"neod1\"},{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":3306,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"mysql\"},{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":3389,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"ms-wbt-server\"},{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":8081,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"blackice-icecap\"},{\"id\":0,\"ip\":\"192.168.43.21\",\"port\":8089,\"protocol\":\"tcp\",\"state\":\"open\",\"createdTime\":\"2019-12-25 16:12:46\",\"service\":\"unknown\"}]";
        System.out.println(GsonUtils.fromJsonArray(jsonData, BackupPort.class));
    }
}
