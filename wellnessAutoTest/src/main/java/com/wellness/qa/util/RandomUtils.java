package com.wellness.qa.util;

import java.util.Random;
/**
 *
 *
 *  会员码的加密解密*
 *  *
 *
 **/
public class RandomUtils {
    private static final String ALL_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LETTER_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBER_CHAR = "0123456789";

    public RandomUtils() {
    }

    public static String generateString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();

        for(int i = 0; i < length; ++i) {
            sb.append("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(random.nextInt("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length())));
        }

        return sb.toString();
    }

    public static String generateMixString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();

        for(int i = 0; i < length; ++i) {
            sb.append("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(random.nextInt("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length())));
        }

        return sb.toString();
    }

    public static String generateLowerString(int length) {
        return generateMixString(length).toLowerCase();
    }

    public static String generateUpperString(int length) {
        return generateMixString(length).toUpperCase();
    }

    public static String generateNumberString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();

        for(int i = 0; i < length; ++i) {
            sb.append("0123456789".charAt(random.nextInt("0123456789".length())));
        }

        return sb.toString();
    }

    //传入uid与时间，返回数字会员码
    public static String randomEnc(Long userId, long times) {
        Integer rand = Integer.valueOf(RandomUtils.generateNumberString(6));
        String[] splitTimes = String.valueOf(times).split("");
        String[] splitUserId = String.valueOf(userId).split("");
        String[] splitRand = String.valueOf(rand).split("");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < splitTimes.length; i++) {
            if (i < splitRand.length) {
                sb.append(splitRand[i]).append(splitUserId[i]).append(splitTimes[i]);
            } else if (i < splitUserId.length && i >= splitRand.length) {
                sb.append(splitUserId[i]).append(splitTimes[i]);
            } else {
                sb.append(splitTimes[i]);
            }
        }
        sb.append(splitUserId.length).append(splitRand.length);
        return sb.toString();
    }
}
