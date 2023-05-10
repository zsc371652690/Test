package com.wellness.qa.util.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wellness.qa.apiUtils.common.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdCodeUtil {
    private static final Logger logger = LoggerFactory.getLogger(com.wellness.qa.apiUtils.common.IdCodeUtil.class);
    private static final int CHINA_ID_LENGTH = 18;
    private static final int[] calcC = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] calcR = new char[]{'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    private static Map<String, String> areaCodeMap = new HashMap();
    private static Map<Integer, String> codeMap = new HashMap();

    public IdCodeUtil() {
    }

    public static String getIdCode() {
        return getIdCode((String)((String)null), (Integer)null);
    }

    public static String getIdCode(String birthDay) {
        return getIdCode((String)birthDay, (Integer)null);
    }

    public static String getIdCode(LocalDateTime birthDay) {
        return getIdCode((String) com.wellness.qa.apiUtils.common.DateUtil.format(birthDay, "yyyyMMdd"), (Integer)null);
    }

    public static String getIdCode(Integer gender) {
        if (gender != 1 && gender != 2) {
            throw new IllegalArgumentException("gender must be 1 or 2");
        } else {
            return getIdCode((String)null, gender);
        }
    }

    public static String getIdCode(LocalDateTime birthDay, Integer gender) {
        return getIdCode(com.wellness.qa.apiUtils.common.DateUtil.format(birthDay, "yyyyMMdd"), gender);
    }

    public static String getIdCode(String birthDay, Integer gender) {
        Random random = new Random();
        int index = random.nextInt(codeMap.size());
        String areaCode = (String)codeMap.get(index);
        if (birthDay == null) {
            birthDay = getRandomBirthday();
        }

        if (gender == null) {
            gender = random.nextInt(2) + 1;
        }

        String sequenceCode = getSequenceCode(gender);
        String checkNumber = getCheckNumber(areaCode + birthDay + sequenceCode);
        return areaCode + birthDay + sequenceCode + checkNumber;
    }

    public static boolean isValidIdCode(String idCode) {
        if (idCode != null && !idCode.isEmpty() && 18 == idCode.length()) {
            if (!validateBirthDay(idCode.substring(6, 14))) {
                return false;
            } else {
                String code17 = idCode.substring(0, 17);
                if (!code17.matches("^\\d+$")) {
                    return false;
                } else {
                    String code18 = idCode.substring(17);
                    String checkNum = getCheckNumber(code17);
                    return code18.equals(checkNum);
                }
            }
        } else {
            return false;
        }
    }

    public static Date getBirthDate(String idCode) {
        if (!isValidIdCode(idCode)) {
            throw new IllegalArgumentException("Illegal idCode: " + idCode);
        } else {
            return com.wellness.qa.apiUtils.common.DateUtil.parseDate(idCode.substring(6, 14), "yyyyMMdd");
        }
    }

    public static int getAge(String idCode) {
        return com.wellness.qa.apiUtils.common.DateUtil.getAge(getBirthDate(idCode), new Date());
    }

    public static int getGender(String idCode) {
        if (!isValidIdCode(idCode)) {
            throw new IllegalArgumentException("Illegal idCode: " + idCode);
        } else {
            char sCardChar = idCode.charAt(16);
            return sCardChar % 2 != 0 ? 1 : 2;
        }
    }

    private static String getRandomBirthday() {
        long begin = System.currentTimeMillis() - 1892160000000L;
        long end = System.currentTimeMillis() - 31536000000L;
        long rnd_age = begin + (long)(Math.random() * (double)(end - begin));
        Date date = new Date(rnd_age);
        return DateUtil.format(date, "yyyyMMdd");
    }

    private static String getSequenceCode(int gender) {
        Random random = new Random();
        String sequenceCode;
        if (gender == 1) {
            sequenceCode = (random.nextInt(499) + 1) * 2 - 1 + "";
        } else {
            sequenceCode = (random.nextInt(499) + 1) * 2 + "";
        }

        while(sequenceCode.length() < 3) {
            sequenceCode = "0" + sequenceCode;
        }

        return sequenceCode;
    }

    private static String getCheckNumber(String code17) {
        int[] n = new int[17];
        int result = 0;

        for(int i = 0; i < n.length; ++i) {
            n[i] = Integer.parseInt(String.valueOf(code17.charAt(i)));
            result += calcC[i] * n[i];
        }

        return String.valueOf(calcR[result % 11]);
    }

    private static boolean validateBirthDay(String birthDay) {
        Pattern p = Pattern.compile("^(\\d{4})(\\d{2})(\\d{2})$");
        Matcher matcher = p.matcher(birthDay);
        if (!matcher.find()) {
            return false;
        } else {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));
            int thisYear = LocalDateTime.now().getYear();
            if (year >= 1900 && year <= thisYear) {
                if (month >= 1 && month <= 12) {
                    if (day >= 1 && day <= 31) {
                        if (day != 31 || month != 4 && month != 6 && month != 9 && month != 11) {
                            return month != 2 || day <= 29 && (day != 29 || (new GregorianCalendar()).isLeapYear(year));
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(getIdCode());
        System.out.println(getIdCode(1));
        System.out.println(getIdCode(LocalDateTime.now().minusYears(20L)));
        System.out.println(isValidIdCode(getIdCode()));
        System.out.println(getAge(getIdCode(LocalDateTime.now().minusYears(20L))));
        System.out.println(getBirthDate(getIdCode(LocalDateTime.now().minusYears(20L))));
        System.out.println(getGender(getIdCode(2)));
    }

    static {
        InputStream is = com.wellness.qa.apiUtils.common.IdCodeUtil.class.getClassLoader().getResourceAsStream("code.txt");
        if (is == null) {
            logger.error("Cannot read code from code.txt");
        } else {
            Reader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);
            int var4 = 0;

            String line;
            try {
                while((line = br.readLine()) != null) {
                    String[] areaCode = line.split(":");
                    if (areaCode[0].length() == 6) {
                        areaCodeMap.put(areaCode[0], areaCode[1]);
                        codeMap.put(var4++, areaCode[0]);
                    }
                }
            } catch (IOException var6) {
                logger.error("Cannot read code from code.txt. ", var6);
            }
        }

    }
}

