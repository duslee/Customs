package com.h3c.ywrj.dzkf.hkcustoms;

import com.h3c.ywrj.dzkf.hkcustoms.common.util.RegexUtils;

import java.util.Arrays;

/**
 * Created by Jeff on 2019-12-21 09:38
 */
public class NmapPortTest {
    public static void main(String[] args) {
//        test1();
//                test2();
//        test3();
        test4();
    }

    private static void test1() {
        System.out.println("53: " + RegexUtils.hasNmapPort("53"));
        System.out.println("53//: " + RegexUtils.hasNmapPort("53//"));
        System.out.println("53/tcp: " + RegexUtils.hasNmapPort("53/tcp"));
        System.out.println("53d/tcp: " + RegexUtils.hasNmapPort("53d/tcp"));
        System.out.println(" 53/tcp: " + RegexUtils.hasNmapPort(" 53/tcp"));
        System.out.println("53/tcp open domain: " + RegexUtils.hasNmapPort("53/tcp open domain"));
        System.out.println(" 53/tcp open   domain: " + RegexUtils.hasNmapPort(" 53/tcp open   domain"));
        System.out.println("53/tcp open   domain: " + RegexUtils.hasNmapPort("53/tcp open   domain"));
    }

    private static void test2() {
        System.out.println("12: " + "12".matches("^\\d+"));
        System.out.println(" 12: " + " 12".matches("^\\d+"));
        System.out.println("12/: " + "12/".matches("^\\d+/"));
        System.out.println("12  : " + "12  ".matches("^\\d+"));
        System.out.println("12/ : " + "12/".matches("^\\d+/"));
        System.out.println("12/   : " + "12/  ".matches("^\\d+/"));
        System.out.println("12/   : " + "12/  ".matches("^\\d+/\\s+"));
        System.out.println("12/tcp : " + "12/tcp".matches("^\\d+/"));
        System.out.println("12/tcp : " + "12/tcp".matches("^\\d+/\\S+"));
        System.out.println("12/tcp : " + "12/tcp  ".matches("^\\d+/\\S+\\s+"));
        System.out.println("12/tcp open    service: " + "12/tcp open    service".matches("^\\d+/\\S+(\\s+\\S+)+"));
    }

    private static void test3() {
        System.out.println(Arrays.toString("12/tcp open    service".split(" ")));
        System.out.println(Arrays.toString("12/tcp open    service ".split(" ")));
        System.out.println(Arrays.toString(" 12/tcp open    service".split(" ")));
        System.out.println(Arrays.toString(" 12/tcp open    service".split("\\s")));
        System.out.println(Arrays.toString("12/tcp open    service ".split("\\s")));
        System.out.println(Arrays.toString(" 12/tcp open    service".split("\\s+")));
        System.out.println(Arrays.toString(" 12/tcp open    service".trim().split("\\s+")));
    }

    private static void test4() {
        System.out.println(Arrays.toString(RegexUtils.split(" ")));
        System.out.println(Arrays.toString(RegexUtils.split(" 123  ")));
        System.out.println(Arrays.toString(RegexUtils.split("12/tcp open    service")));
        System.out.println(Arrays.toString(RegexUtils.split(" 12/tcp open    service")));
        System.out.println(Arrays.toString(RegexUtils.split(" 12/tcp open    service".trim())));
    }
}
