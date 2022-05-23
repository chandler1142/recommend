package com.chandler.demo.recommend.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionContainer {

    public static final Map<String, String> userTokenMap = new ConcurrentHashMap();

}
