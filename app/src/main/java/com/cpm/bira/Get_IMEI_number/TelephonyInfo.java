package com.cpm.bira.Get_IMEI_number;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

/**
 * Created by deepakp on 9/27/2017.
 */

public class TelephonyInfo {

    private static TelephonyInfo telephonyInfo;
    private String imeiSIM1;
    private String imeiSIM2;

    public String getImeiSIM1() {
        return imeiSIM1;
    }

    public String getImeiSIM2() {
        return imeiSIM2;
    }


    public boolean isDualSIM() {
        return imeiSIM2 != null;
    }


    public static TelephonyInfo getInstance(Context context) {

        if (telephonyInfo == null) {

            telephonyInfo = new TelephonyInfo();

            telephonyInfo.imeiSIM1 = null;
            telephonyInfo.imeiSIM2 = null;
            try {
                telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceIdGemini", 0);
                telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceIdGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();

                try {
                    telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceId", 0);
                    telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceId", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                    try {
                        telephonyInfo.imeiSIM1 = getDeviceIdBySlotNew(context, "getDeviceId", 0);
                        telephonyInfo.imeiSIM2 = getDeviceIdBySlotNew(context, "getDeviceId", 1);
                    } catch (GeminiMethodNotFoundException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }

        return telephonyInfo;
    }


    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        String imei = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);


            if (ob_phone != null) {
                imei = ob_phone.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof NoSuchMethodException && predictedMethodName.equalsIgnoreCase("getDeviceId")) {
                Class<?> telephonyClass = null;
                try {
                    telephonyClass = Class.forName(telephony.getClass().getName());
                    Class<?>[] parameter = new Class[1];
                    parameter[0] = int.class;
                    Method getSimID = telephonyClass.getMethod("getDefault", parameter);
                    Object[] obParameter = new Object[1];
                    obParameter[0] = slotID;
                    //TelephonyManager ob_phone = (TelephonyManager) getSimID.invoke(null, obParameter);
                    TelephonyManager ob_phone = (TelephonyManager) getSimID.invoke(telephony, obParameter);
                    if (ob_phone != null) {
                        imei = ob_phone.getDeviceId().toString();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    throw new GeminiMethodNotFoundException(predictedMethodName);
                }

            } else {
                throw new GeminiMethodNotFoundException(predictedMethodName);
            }

        }

        return imei;
    }

    private static String getDeviceIdBySlotNew(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
        String imei = "0";
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (telephony.getDeviceId() != null) {
                imei = telephony.getDeviceId().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imei;
    }


    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }

}
