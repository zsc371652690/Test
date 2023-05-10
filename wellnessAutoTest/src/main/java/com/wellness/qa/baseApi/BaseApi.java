package com.wellness.qa.baseApi;


import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.SHttpTask;
import com.ejlchina.okhttps.jackson.JacksonMsgConvertor;

import com.wellness.qa.config.EnvConfig;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;


@Slf4j
public class BaseApi {
    public static HTTP gHttp =  HTTP.builder()
            .addMsgConvertor(new JacksonMsgConvertor())
            .baseUrl(EnvConfig.ENV.get("gBaseUrl"))
            .build();

    public static HTTP adminHttp =  HTTP.builder()
            .addMsgConvertor(new JacksonMsgConvertor())
            .baseUrl(EnvConfig.ENV.get("adminBaseUrl"))
            .build();

    public static HTTP checkHttp =  HTTP.builder()
            .addMsgConvertor(new JacksonMsgConvertor())
            .baseUrl(EnvConfig.ENV.get("checkInBaseUrl"))
            .build();


    private  static HttpResult execute(SHttpTask httpTask, String method, String token){
        httpTask.nothrow();
        if(StringUtils.isNotBlank(token)){
            httpTask.addHeader("auth-token",token);
        }


        log.info("http请求：{} {}\n headers:{}\ncontent-type:{}\nrequestBody:{}\nbodyParas:{}",
                method.toUpperCase(), httpTask.getUrl(), httpTask.getHeaders(),
                httpTask.getBodyType(),new JacksonMsgConvertor().serialize(httpTask.getRequestBody()),
                new JacksonMsgConvertor().serialize(httpTask.getBodyParas()));

        Allure.step("http请求",()->{
            Allure.addAttachment("请求Url",httpTask.getUrl());
            Allure.addAttachment("请求方法", method);
            if(httpTask.getHeaders()!=null){
                Allure.addAttachment("请求Headers",httpTask.getHeaders().toString());
            }
        });
        HttpResult httpResult;
        switch (method.toLowerCase()){
            case "get":
                httpResult=httpTask.get();
                break;
            case "post":
                httpResult=httpTask.post();
                break;
            case "head":
                httpResult=httpTask.head();
                break;
            case "put":
                httpResult=httpTask.put();
                break;
            case "patch":
                httpResult=httpTask.patch();
                break;
            case "delete":
                httpResult=httpTask.delete();
                break;
            default:
                httpResult=httpTask.request(method);
        }
        log.info("httpResult： {}\n{}",httpResult,httpResult.getBody().cache());

        Allure.step(
                "http响应",()->{
                    Allure.addAttachment("响应Header",httpResult.allHeaders().toString());

                    Allure.addAttachment("响应Body","application/json" ,httpResult.getBody().cache().toString());
                });



        return httpResult;
    }

    public static HttpResult get(SHttpTask httpTask){
        return execute(httpTask,"get",null);
    }

    public static HttpResult get(SHttpTask httpTask, String token){
        return execute(httpTask,"get",token);
    }

    public static HttpResult  post(SHttpTask httpTask){
        return execute(httpTask,"post",null);
    }

    public static HttpResult post(SHttpTask httpTask,String token){
        return execute(httpTask,"post",token);
    }

    public static HttpResult head(SHttpTask httpTask, String token){
        return execute(httpTask,"head",token);
    }

    public static HttpResult put(SHttpTask httpTask, String token){
        return execute(httpTask,"put",token);
    }

    public static HttpResult patch(SHttpTask httpTask, String token){
        return execute(httpTask,"patch",token);
    }

    public static HttpResult delete(SHttpTask httpTask, String token){
        return execute(httpTask,"delete",token);
    }

}
