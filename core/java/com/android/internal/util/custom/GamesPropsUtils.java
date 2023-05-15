/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.util.custom;

import android.os.Build;
import android.util.Log;

import java.util.Arrays;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class GamesPropsUtils {

    private static final String TAG = GamesPropsUtils.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final Map<String, Object> propsToChangeROG6;
    private static final String[] packagesToChangeROG6 = {
    	    "com.ea.gp.fifamobile",
            "com.activision.callofduty.shooter",
            "com.madfingergames.legends",
            "com.mobile.legends",
            "com.pearlabyss.blackdesertm",
            "com.pearlabyss.blackdesertm.gl"
    };

    private static final Map<String, Object> propsToChangeXP5;
    private static final String[] packagesToChangeXP5 = {
            "com.tencent.tmgp.kr.codm",
            "com.garena.game.codm",
            "com.vng.codmvn"
    };

    private static final Map<String, Object> propsToChangeOP8P;
    private static final String[] packagesToChangeOP8P = {
            "com.tencent.ig",
            "com.pubg.imobile",
            "com.pubg.krmobile",
            "com.vng.pubgmobile",
            "com.rekoo.pubgm",
            "com.tencent.tmgp.pubgmhd",
            "com.riotgames.league.wildrift",
            "com.riotgames.league.wildrifttw",
            "com.riotgames.league.wildriftvn",
            "com.netease.lztgglobal"
    };

    private static final Map<String, Object> propsToChangeOP9P;
    private static final String[] packagesToChangeOP9P = {
            "com.epicgames.fortnite",
            "com.epicgames.portal",
            "com.tencent.lolm",
            "com.tencent.tmgp.sgame"
    };

    private static final Map<String, Object> propsToChangeF4;
    private static final String[] packagesToChangeF4 = {
            "com.dts.freefiremax",
            "com.dts.freefireth"
    };

    private static final Map<String, Object> propsToChange11TP;
    private static final String[] packagesToChange11TP = {
            "com.ea.gp.apexlegendsmobilefps",
            "com.levelinfinite.hotta.gp",
            "com.vng.mlbbvn"
    };

    static {
        propsToChangeROG6 = new HashMap<>();
        propsToChangeROG6.put("BRAND", "asus");
        propsToChangeROG6.put("MANUFACTURER", "asus");
        propsToChangeROG6.put("DEVICE", "AI2201");
        propsToChangeROG6.put("MODEL", "ASUS_AI2201");
        propsToChangeXP5 = new HashMap<>();
        propsToChangeXP5.put("BRAND", "Sony");
        propsToChangeXP5.put("MANUFACTURER", "Sony");
        propsToChangeXP5.put("DEVICE", "SO-52A");
        propsToChangeXP5.put("MODEL", "SO-52A");
        propsToChangeOP8P = new HashMap<>();
        propsToChangeOP8P.put("BRAND", "OnePlus");
        propsToChangeOP8P.put("MANUFACTURER", "OnePlus");
        propsToChangeOP8P.put("DEVICE", "OnePlus8Pro");
        propsToChangeOP8P.put("MODEL", "IN2020");
        propsToChangeOP9P = new HashMap<>();
        propsToChangeOP9P.put("BRAND", "OnePlus");
        propsToChangeOP9P.put("MANUFACTURER", "OnePlus");
        propsToChangeOP9P.put("DEVICE", "OnePlus9Pro");
        propsToChangeOP9P.put("MODEL", "LE2123");
        propsToChange11TP = new HashMap<>();
        propsToChange11TP.put("BRAND", "Xiaomi");
        propsToChange11TP.put("MANUFACTURER", "Xiaomi");
        propsToChange11TP.put("DEVICE", "vili");
        propsToChange11TP.put("MODEL", "2107113SI");
        propsToChangeF4 = new HashMap<>();
        propsToChangeF4.put("BRAND", "Xiaomi");
        propsToChangeF4.put("MANUFACTURER", "Xiaomi");
        propsToChangeF4.put("DEVICE", "munch_global");
        propsToChangeF4.put("MODEL", "22021211RG");
    }

    public static void setProps(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return;
        }
        if (Arrays.asList(packagesToChangeROG6).contains(packageName)){
            if (DEBUG){
                Log.d(TAG, "Defining props for: " + packageName);
            }
            for (Map.Entry<String, Object> prop : propsToChangeROG6.entrySet()) {
                String key = prop.getKey();
                Object value = prop.getValue();
                setPropValue(key, value);
            }
        }
        if (Arrays.asList(packagesToChangeXP5).contains(packageName)){
            if (DEBUG){
                Log.d(TAG, "Defining props for: " + packageName);
            }
            for (Map.Entry<String, Object> prop : propsToChangeXP5.entrySet()) {
                String key = prop.getKey();
                Object value = prop.getValue();
                setPropValue(key, value);
            }
        }
        if (Arrays.asList(packagesToChangeOP8P).contains(packageName)){
            if (DEBUG){
                Log.d(TAG, "Defining props for: " + packageName);
            }
            for (Map.Entry<String, Object> prop : propsToChangeOP8P.entrySet()) {
                String key = prop.getKey();
                Object value = prop.getValue();
                setPropValue(key, value);
            }
        }
        if (Arrays.asList(packagesToChangeOP9P).contains(packageName)){
            if (DEBUG){
                Log.d(TAG, "Defining props for: " + packageName);
            }
            for (Map.Entry<String, Object> prop : propsToChangeOP9P.entrySet()) {
                String key = prop.getKey();
                Object value = prop.getValue();
                setPropValue(key, value);
            }
        }
        if (Arrays.asList(packagesToChange11TP).contains(packageName)){
            if (DEBUG){
                Log.d(TAG, "Defining props for: " + packageName);
            }
            for (Map.Entry<String, Object> prop : propsToChange11TP.entrySet()) {
                String key = prop.getKey();
                Object value = prop.getValue();
                setPropValue(key, value);
            }
        }
        if (Arrays.asList(packagesToChangeF4).contains(packageName)){
            if (DEBUG){
                Log.d(TAG, "Defining props for: " + packageName);
            }
            for (Map.Entry<String, Object> prop : propsToChangeF4.entrySet()) {
                String key = prop.getKey();
                Object value = prop.getValue();
                setPropValue(key, value);
            }
        }
    }

    private static void setPropValue(String key, Object value){
        try {
            if (DEBUG){
                Log.d(TAG, "Defining prop " + key + " to " + value.toString());
            }
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to set prop " + key, e);
        }
    }
}
