package com.h3c.ywrj.dzkf.hkcustoms;

/**
 * Created by wfw2525 on 2019/12/10 14:56
 */
public class ScanPortThread extends Thread {
    private String ip;

    public ScanPortThread(String ip) {
        this.ip = ip;
    }

    @Override
    public void run() {
//        String data = new NmapTest().getScanData("/usr/local/Cellar/nmap/7.80_1/bin/nmap", ip);
        String data = new NmapTest().getScanData("G:\\hs_work\\Project\\HaiKouCustoms\\tools\\nmap-7.80\\nmap.exe", ip);
//        System.out.println(data);
    }
}
