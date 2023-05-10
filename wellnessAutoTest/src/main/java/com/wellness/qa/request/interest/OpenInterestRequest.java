package com.wellness.qa.request.interest;

import lombok.Data;

@Data
public class OpenInterestRequest {

    private String activeDate;

    private String userInterestId;

    @Override
    public String toString() {
        return "OpenInterestRequest{" +
                "activeDate='" + activeDate + '\'' +
                ", userInterestId='" + userInterestId + '\'' +
                '}';
    }
}
