package com.h3c.ywrj.dzkf.hkcustoms.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Author   @author wfw2525
 * Date     2018/10/24
 * Time     9:54
 */
@Slf4j
public final class GsonUtils {
    private static final Gson sGson = new Gson();
    private static final JsonParser sJsonParser = new JsonParser();

    public static Gson getGson() {
        return sGson;
    }

    public static JsonParser getJsonParser() {
        return sJsonParser;
    }

    public static <T> T fromJson(String jsonData, Class<T> clazz) {
        return sGson.fromJson(jsonData, clazz);
    }

    public static <T> T fromJson(JsonElement element, Class<T> clazz) {
        return sGson.fromJson(element, clazz);
    }

    public static <T> List<T> fromJsonArray(String jsonData, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        final JsonArray jsonArray = sJsonParser.parse(jsonData).getAsJsonArray();
        for (JsonElement element : jsonArray) list.add(fromJson(element, clazz));
        return list;
    }

    public static String toJson(Object obj) {
        return sGson.toJson(obj);
    }

//    public static void main(String[] args) {
//        List<Dev> list = new ArrayList<>();
//        Dev dev;
//        Random random = new Random();
//        for (int i = 1; i < 10; i++) {
//            dev = new Dev();
//            dev.setIpAddr("10.89.10." + i);
//            dev.setDescription("10.89.10." + i);
//            dev.setLocation("位置" + i);
//            dev.setRegionName("宏伟区街道2");
//            dev.setState(random.nextInt(3));
//            dev.setDelayTime(12.0);
//            list.add(dev);
//        }
//
//        System.out.println(toJson(list));
//    }
}
