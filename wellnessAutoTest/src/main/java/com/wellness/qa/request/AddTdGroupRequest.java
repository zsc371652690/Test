package com.wellness.qa.request;

import lombok.Data;

import java.util.List;

@Data
public class AddTdGroupRequest {

    private  String tdgroupName;

    private  String tdgroupCode;

    private  String tdgroupDesc;

    private List storeList;

}
