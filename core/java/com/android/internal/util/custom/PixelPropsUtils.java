/*
 * Copyright (C) 2022 The Pixel Experience Project
 *               2021-2022 crDroid Android Project
 *           (C) 2023 ArrowOS
 *           (C) 2023 The LibreMobileOS Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.util.custom;

import android.app.ActivityTaskManager;
import android.app.Application;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Build;
import android.os.Process;
import android.os.SystemProperties;
import android.util.Log;

import com.android.internal.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PixelPropsUtils {

    private static final String TAG = PixelPropsUtils.class.getSimpleName();
    private static final String DEVICE = "org.pixelexperience.device";

    private static final ComponentName GMS_ADD_ACCOUNT_ACTIVITY = ComponentName.unflattenFromString(
            "com.google.android.gms/.auth.uiflows.minutemaid.MinuteMaidActivity");

    private static final boolean DEBUG = false;

    private static final String[] sCertifiedProps =
            Resources.getSystem().getStringArray(R.array.config_certifiedBuildProperties);

    private static final Map<String, Object> propsToChangeGeneric;

    private static final Map<String, Object> propsToChangePixel8Pro =
            createGoogleSpoofProps("husky", "Pixel 8 Pro",
                    "google/husky/husky:14/UD1A.230803.041/10808477:user/release-keys");

    private static final Map<String, Object> propsToChangeQcomPixel =
            createGoogleSpoofProps("barbet", "Pixel 5a",
                    "google/barbet/barbet:14/UP1A.231005.007/10754064:user/release-keys");

    private static final Map<String, Object> propsToChangePixelXL =
            createGoogleSpoofProps("marlin", "Pixel XL",
                    "google/marlin/marlin:10/QP1A.191005.007.A3/5972272:user/release-keys");

    private static final Map<String, ArrayList<String>> propsToKeep;

    private static final String[] packagesToChangePixel8Pro = {
            "com.google.android.apps.customization.pixel",
            "com.google.android.apps.privacy.wildlife",
            "com.google.android.apps.wallpaper.pixel",
            "com.google.android.apps.wallpaper",
            "com.google.android.apps.subscriptions.red",
            "com.google.pixel.livewallpaper",
            "com.google.android.wallpaper.effects",
            "com.google.android.apps.emojiwallpaper",
            "com.google.android.apps.aiwallpapers"
    };

    private static final String[] extraPackagesToChange = {
            "com.android.chrome",
            "com.breel.wallpapers20",
            "com.nhs.online.nhsonline",
            "com.netflix.mediaclient",
            "com.nothing.smartcenter"
    };

    private static final String[] packagesToKeep = {
            "com.google.android.apps.motionsense.bridge",
            "com.google.android.apps.pixelmigrate",
            "com.google.android.dialer",
            "com.google.android.euicc",
            "com.google.ar.core",
            "com.google.android.youtube",
            "com.google.android.apps.youtube.kids",
            "com.google.android.apps.youtube.music",
            "com.google.android.apps.recorder",
            "com.google.android.apps.wearables.maestro.companion",
            "com.google.android.apps.tachyon",
            "com.google.android.apps.tycho",
            "com.google.android.as",
            "com.google.android.gms",
            "com.google.android.apps.restore",
            "com.google.oslo"
    };

    private static final String[] customGoogleCameraPackages = {
            "com.google.android.MTCL83",
            "com.google.android.UltraCVM",
            "com.google.android.apps.cameralite"
    };

    // Codenames for currently supported Pixels by Google
    private static final String[] pixelCodenames = {
            "husky",
            "shiba",
            "felix",
            "tangorpro",
            "lynx",
            "cheetah",
            "panther",
            "bluejay",
            "oriole",
            "raven",
            "barbet",
            "redfin",
            "bramble",
            "sunfish"
    };

    private static volatile boolean sIsGms, sIsFinsky, sIsSetupWizard;

    static {
        propsToKeep = new HashMap<>();
        propsToKeep.put("com.google.android.settings.intelligence", new ArrayList<>(Collections.singletonList("FINGERPRINT")));
        propsToChangeGeneric = new HashMap<>();
        propsToChangeGeneric.put("TYPE", "user");
        propsToChangeGeneric.put("TAGS", "release-keys");
    }

    private static String getBuildID(String fingerprint) {
        Pattern pattern = Pattern.compile("([A-Za-z0-9]+\\.\\d+\\.\\d+\\.\\w+)");
        Matcher matcher = pattern.matcher(fingerprint);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private static String getDeviceName(String fingerprint) {
        String[] parts = fingerprint.split("/");
        if (parts.length >= 2) {
            return parts[1];
        }
        return "";
    }

    private static Map<String, Object> createGoogleSpoofProps(String device, String model, String fingerprint) {
        Map<String, Object> props = new HashMap<>();
        props.put("BRAND", "google");
        props.put("MANUFACTURER", "Google");
        props.put("ID", getBuildID(fingerprint));
        props.put("DEVICE", device);
        props.put("PRODUCT", device);
        props.put("MODEL", model);
        props.put("FINGERPRINT", fingerprint);
        props.put("TYPE", "user");
        props.put("TAGS", "release-keys");
        return props;
    }

    private static boolean isGoogleCameraPackage(String packageName) {
        return packageName.startsWith("com.google.android.GoogleCamera") ||
                Arrays.asList(customGoogleCameraPackages).contains(packageName);
    }
    
    
    private static boolean shouldTryToCertifyDevice() {
        if (!sIsGms) return false;

        final String processName = Application.getProcessName();
        if (!processName.toLowerCase().contains("unstable")
                && !processName.toLowerCase().contains("pixelmigrate")
                && !processName.toLowerCase().contains("instrumentation")) {
            return false;
        }

        final boolean[] shouldCertify = {true};

        setBuildField("TIME", System.currentTimeMillis());

        final boolean was = isGmsAddAccountActivityOnTop();
        final String reason = "GmsAddAccountActivityOnTop";
        if (!was) {
            spoofBuildGms();
        }
        dlog("Skip spoofing build for GMS, because " + reason + "!");
        TaskStackListener taskStackListener = new TaskStackListener() {
            @Override
            public void onTaskStackChanged() {
                final boolean isNow = isGmsAddAccountActivityOnTop();
                if (isNow ^ was) {
                    dlog(String.format("%s changed: isNow=%b, was=%b, killing myself!", reason, isNow, was));
                    shouldCertify[0] = false;
                }
            }
        };
        try {
            ActivityTaskManager.getService().registerTaskStackListener(taskStackListener);
        } catch (Exception e) {
            Log.e(TAG, "Failed to register task stack listener!", e);
            spoofBuildGms();
        }
        if (shouldCertify[0]) {
            try {
                ActivityTaskManager.getService().unregisterTaskStackListener(taskStackListener); // this will be registered on next query
            } catch (Exception e) {}
        }
        return shouldCertify[0];
    }

    private static void spoofBuildGms() {
        if (sCertifiedProps == null || sCertifiedProps.length == 0) return;
        // Alter model name and fingerprint to avoid hardware attestation enforcement
        setBuildField("PRODUCT", sCertifiedProps[0].isEmpty() ? getDeviceName(sCertifiedProps[4]) : sCertifiedProps[0]);
        setBuildField("DEVICE", sCertifiedProps[1].isEmpty() ? getDeviceName(sCertifiedProps[4]) : sCertifiedProps[1]);
        setBuildField("MANUFACTURER", sCertifiedProps[2]);
        setBuildField("BRAND", sCertifiedProps[3]);
        setBuildField("MODEL", sCertifiedProps[4]);
        setBuildField("FINGERPRINT", sCertifiedProps[5]);
        if (!sCertifiedProps[6].isEmpty()) {
            setVersionFieldString("SECURITY_PATCH", sCertifiedProps[6]);
        }
        if (!sCertifiedProps[7].isEmpty() && sCertifiedProps[7].matches("\\d+")) {
            setVersionFieldInt("DEVICE_INITIAL_SDK_INT", Integer.parseInt(sCertifiedProps[7]));
        }
        setBuildField("ID", sCertifiedProps[8].isEmpty() ? getBuildID(sCertifiedProps[4]) : sCertifiedProps[8]);
        setBuildField("TYPE", sCertifiedProps[9].isEmpty() ? "user" : sCertifiedProps[9]);
        setBuildField("TAGS", sCertifiedProps[10].isEmpty() ? "release-keys" : sCertifiedProps[10]);
    }

    public static void setProps(String packageName) {
        propsToChangeGeneric.forEach((k, v) -> setBuildField(k, v));
        if (packageName == null || packageName.isEmpty()) {
            return;
        }

        if (packageName.equals("com.android.vending")) {
            sIsFinsky = true;
        } else if (packageName.equals("com.google.android.gms")) {
            sIsGms = true;
        } else if (packageName.equals("com.google.android.setupwizard")) {
            sIsSetupWizard = true;
        }
        if (shouldTryToCertifyDevice()) {
            return;
        }

        if (Arrays.asList(packagesToKeep).contains(packageName)) {
            return;
        }
        if (isGoogleCameraPackage(packageName)) {
            return;
        }

        Map<String, Object> propsToChange = new HashMap<>();

        if (packageName.startsWith("com.google.")
                || packageName.startsWith("com.samsung.")
                || Arrays.asList(extraPackagesToChange).contains(packageName)) {

            boolean isPixelDevice = Arrays.asList(pixelCodenames).contains(SystemProperties.get(DEVICE));

            if (packageName.equals("com.google.android.apps.photos")) {
                propsToChange.putAll(propsToChangePixelXL);
            } else if (isPixelDevice) {
                return;
            } else {
                if (Arrays.asList(packagesToChangePixel8Pro).contains(packageName)) {
                    propsToChange.putAll(propsToChangePixel8Pro);
                } else {
                    propsToChange.putAll(propsToChangeQcomPixel);
                }
            }

            if (DEBUG) Log.d(TAG, "Defining props for: " + packageName);
            for (Map.Entry<String, Object> prop : propsToChange.entrySet()) {
                String key = prop.getKey();
                Object value = prop.getValue();
                if (propsToKeep.containsKey(packageName) && propsToKeep.get(packageName).contains(key)) {
                    if (DEBUG) Log.d(TAG, "Not defining " + key + " prop for: " + packageName);
                    continue;
                }
                if (DEBUG) Log.d(TAG, "Defining " + key + " prop for: " + packageName);
                setBuildField(key, value);
            }
            // Set proper indexing fingerprint
            if (packageName.equals("com.google.android.settings.intelligence")) {
                setBuildField("FINGERPRINT", Build.VERSION.INCREMENTAL);
            }
        }
    }

    private static void setBuildField(String key, Object value) {
        try {
            if (DEBUG) Log.d(TAG, "Defining prop " + key + " to " + value.toString());
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to set prop " + key, e);
        }
    }

    private static void setBuildField(String key, String value) {
        try {
            // Unlock
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);

            // Edit
            field.set(null, value);

            // Lock
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to spoof Build." + key, e);
        }
    }

    private static void setVersionFieldInt(String key, int value) {
        try {
            dlog("Defining version field " + key + " to " + value);
            Field field = Build.VERSION.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to spoof Build." + key, e);
        }
    }

    private static void setVersionFieldString(String key, String value) {
        try {
            Field field = Build.VERSION.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to spoof Build." + key, e);
        }
    }

    private static boolean isGmsAddAccountActivityOnTop() {
        try {
            final ActivityTaskManager.RootTaskInfo focusedTask =
                    ActivityTaskManager.getService().getFocusedRootTaskInfo();
            return focusedTask != null && focusedTask.topActivity != null
                    && focusedTask.topActivity.equals(GMS_ADD_ACCOUNT_ACTIVITY);
        } catch (Exception e) {
            Log.e(TAG, "Unable to get top activity!", e);
        }
        return false;
    }

    public static boolean shouldBypassTaskPermission(Context context) {
        // GMS doesn't have MANAGE_ACTIVITY_TASKS permission
        final int callingUid = Binder.getCallingUid();
        final String callingPackage = context.getPackageManager().getNameForUid(callingUid);
        dlog("shouldBypassTaskPermission: callingPackage:" + callingPackage);
        return callingPackage != null && callingPackage.toLowerCase().contains("google");
    }

    private static boolean isCallerSafetyNet() {
        return sIsGms && Arrays.stream(Thread.currentThread().getStackTrace())
                            .anyMatch(elem -> elem.getClassName().toLowerCase()
                                .contains("droidguard"));
    }

    public static void onEngineGetCertificateChain() {
        // Check stack for SafetyNet or Play Integrity
        if ((isCallerSafetyNet() || sIsFinsky) && !sIsSetupWizard && shouldTryToCertifyDevice()) {
            dlog("Blocked key attestation sIsGms=" + sIsGms + " sIsFinsky=" + sIsFinsky);
            throw new UnsupportedOperationException();
        }
    }

    public static void dlog(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }

}
