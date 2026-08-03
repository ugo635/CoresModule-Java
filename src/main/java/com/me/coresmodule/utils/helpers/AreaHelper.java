package com.me.coresmodule.utils.helpers;

import com.me.coresmodule.utils.TabList;

public class AreaHelper {

    public static boolean isInGarden() {
        String area = TabList.findInfo("Area: ");
        return (area == null ? "None" : area).contains("Garden");
    }

    public static boolean isInHub() {
        String area = TabList.findInfo("Area: ");
        return (area == null ? "None" : area).contains("Hub");
    }
}
