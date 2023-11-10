package com.h3c.ywrj.dzkf.hkcustoms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by wfw2525 on 2019/12/10 14:57
 */
public class NmapTest {
    public String getScanData(String cmdDir, String ip) {
        Process process = null;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            process = Runtime.getRuntime().exec(cmdDir + " " + ip);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != process) process.destroy();
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(System.getProperties().getProperty("os.name"));
        String[] ips = new String[]{"10.124.10.76"};
//        String[] ips = new String[]{"169.254.17.123"};
        System.out.println("<===== " + new Date() + " ======");
        for (String ip : ips) new ScanPortThread(ip).start();
        System.out.println("====== " + new Date() + " =====>");
    }
}
