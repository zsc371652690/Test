package com.wellness.qa.enums.responseCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum ResponseEnum {
    FAILED("errCode","errMessage","success"),
    SUCCESS("code","message","success"),
    ;
    private String code;
    private String msg;
    private String success;

}
