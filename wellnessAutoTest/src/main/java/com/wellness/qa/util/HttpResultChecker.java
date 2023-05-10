package com.wellness.qa.util;

import com.ejlchina.okhttps.HttpResult;
import com.jayway.jsonpath.JsonPath;
import com.wellness.qa.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpResultChecker {
    private HttpResult result;
    private HttpResult.Body  body;

    public HttpResultChecker(HttpResult result) {
        this.result = result;
        this.body = result.getBody().cache();
    }

    public  void success(){
        success(1, "operation success",true);

    }
    public void success(Integer code, boolean success){
        success(code, null,success);

    }

    public void success( boolean success){
        success(null, null ,success);

    }

    public void success(String msg, boolean success){
        success(null, msg,success);

    }

    public void success(IExceptionCodeEnum exceptionCodeEnum){
        success(exceptionCodeEnum.getCode(), exceptionCodeEnum.getDescCN(), false);
    }

    public void success( Integer code,String msg, boolean success ){
        assertEquals(200, this.result.getStatus(),"检查响应返回码为200");
        assertAll("检查响应",
                //验证返回message
                ()->{
                    if(null!=code){
                        assertThat("检查code",  JsonPath.read(body.toString(),"$.code"), equalTo(code));
                    }
                },
                ()->{
                    if(null!=msg){
                        assertThat("检查message",  JsonPath.read(body.toString(), "$.message"), is(msg));
                    }
                },
                ()->assertThat( "success", JsonPath.read(body.toString(), "$.success"), is(success))
        );
    }


    public void fail( String code,String msg, boolean success ){
        assertEquals(200, this.result.getStatus(),"检查响应返回码为200");
        assertAll("检查响应",
                //验证返回message
                ()->{
                    if(null!=code){
                        assertThat("检查code",  JsonPath.read(body.toString(),"$.errCode"), equalTo(code));
                    }
                },
                ()->{
                    if(null!=msg){
                        assertThat("检查message",  JsonPath.read(body.toString(), "$.errMessage"), is(msg));
                    }
                },
                ()->assertThat( "success", JsonPath.read(body.toString(), "$.success"), is(success))
        );
    }
}
