package com.wellness.qa.util;

import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;
import com.ejlchina.okhttps.jackson.JacksonMsgConvertor;
import com.jayway.jsonpath.JsonPath;
import com.wellness.qa.config.EnvConfig;
import lombok.extern.slf4j.Slf4j;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class TokenUtil {

    /**
     * 生成token
     *
     * @param loginType：3-微信   4-企业微信   5-后台管理  6-智能前台  7-tools后台
//     * @param roleType：
     *                MEMBER(1, "会籍"),
     *     PRIVATE(2, "私教"),
     *     OPERATION(3, "运营"),
     *     GROUP(4, "团操"),
     *     KANG_DOCTOR(6, "康博士"),
     *     DEVELOP(8, "总部"),
     * @param userId
//     * @param userName
     * @return  该userId生成的token
     */
    public  static String getToken(long userId, Integer loginType){

        String token = generateToken(userId,loginType);

        return token;
    }

    private  static String generateToken(long userId, int loginType){
        String baseUrl;
        if(loginType==5){
//            baseUrl= EnvConfig.ENV.get("adminBaseUrl");
            baseUrl = "https://g-fat.1012china.com";
        }else if (loginType==7){
            baseUrl = "https://g-fat.1012china.com";
        }
        else if (loginType==6){
            baseUrl = "https://checkin-fat.1012wellness.com";
        }
        else {
            baseUrl=EnvConfig.ENV.get("gBaseUrl");
        }
        HttpResult httpResult = HTTP.builder()
                .baseUrl( baseUrl)
                .bodyType(OkHttps.JSON)
                .addMsgConvertor(new JacksonMsgConvertor())
                .build()
                .sync("/1012wellness-user-service/login/createTokenReturnIfExist")
                .addBodyPara("loginType",loginType)
                .addBodyPara("userId",userId)
                .nothrow()
                .post();

        assertThat("获取token成功",httpResult.isSuccessful(),is(true));

        String token= JsonPath.read(httpResult.getBody().toString(),"$.data.token");
        log.debug("获取token：{}",token);
        return token;
    }
}
