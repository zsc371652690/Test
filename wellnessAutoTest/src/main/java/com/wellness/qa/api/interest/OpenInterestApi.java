package com.wellness.qa.api.interest;

import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;
import com.ejlchina.okhttps.SHttpTask;
import com.wellness.qa.request.interest.OpenInterestRequest;
import io.qameta.allure.Step;

import static com.wellness.qa.baseApi.BaseApi.gHttp;
import static com.wellness.qa.baseApi.BaseApi.post;

public class OpenInterestApi {

    @Step("激活权益")
    public static HttpResult openInterestRequest(OpenInterestRequest openInterestRequest, String token){
        SHttpTask httpTask = gHttp.sync("/1012wellness-furnace-service/furnace/interest/openInterest").nothrow()
                .setBodyPara(openInterestRequest)
                .bodyType(OkHttps.JSON);
        return post(httpTask,token);
    }
}
