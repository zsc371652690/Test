package com.wellness.qa.api;

import com.wellness.qa.baseApi.BaseApi;
import com.wellness.qa.request.AddTdGroupRequest;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;
import com.ejlchina.okhttps.SHttpTask;
import io.qameta.allure.Step;

public class AddTdGroupApi extends BaseApi {

    @Step("新增单店通店组")
    public static HttpResult addTdGroupRequest(AddTdGroupRequest addTdGroupRequest, String token){
        SHttpTask httpTask = adminHttp.sync("/api/tdGroup/add").nothrow()
                .setBodyPara(addTdGroupRequest)
                .bodyType(OkHttps.JSON);
        return post(httpTask,token);
    }
}
