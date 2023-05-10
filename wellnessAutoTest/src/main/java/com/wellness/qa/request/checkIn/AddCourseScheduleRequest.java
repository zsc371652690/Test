package com.wellness.qa.request.checkIn;


import lombok.Data;

@Data
public class AddCourseScheduleRequest {

    private String capacity;

    private int categoryId;

    private String categoryName;

    private String chargesNature;

    private String chargesNatureCode;

    private String coachId;

    private String coachName;

    private String courseDate;

    private int courseDuration;

    private int courseId;

    private String courseName;

    private String courseNature;

    private String courseTime;

    private String fieldCode;

    private String fieldName;

    private String storeCode;

    private String storeName;



}
