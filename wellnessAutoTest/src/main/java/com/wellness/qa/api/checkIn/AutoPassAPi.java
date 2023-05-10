package com.wellness.qa.api.checkIn;

import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;
import com.ejlchina.okhttps.SHttpTask;
import com.wellness.qa.request.checkIn.AddCourseScheduleRequest;
import com.wellness.qa.request.checkIn.AutoPassRequest;
import io.qameta.allure.Step;

import static com.wellness.qa.baseApi.BaseApi.*;

public class AutoPassAPi {
    @Step("手动进场")
    public static HttpResult autoPassApi(AutoPassRequest autoPassRequest, String token){
        SHttpTask httpTask = checkHttp.sync("/api/ck/pass/autoPass").nothrow()
                .setBodyPara(autoPassRequest)
                .bodyType(OkHttps.JSON);
        return post(httpTask,token);
    }

    @Step("手动添加团操课")
    public static HttpResult addCourseSchedule(AddCourseScheduleRequest autoPassRequest, String token){
        SHttpTask httpTask = adminHttp.sync("/api/admin/gymnaestrada/courseSchedule/add").nothrow()
                .setBodyPara(autoPassRequest)
                .bodyType(OkHttps.JSON);
        return post(httpTask,token);

    }

    @Step("审核通过团操课")
    public static HttpResult batchAudit(long ids, String token){
        SHttpTask httpTask = adminHttp.sync("/api/admin/gymnaestrada/courseSchedule/batchAudit").nothrow()
                .addUrlPara("ids",ids);
        return get(httpTask,token);

    }
}
