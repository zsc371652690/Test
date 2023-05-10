package com.wellness.qa.request.checkIn;

import lombok.Data;

@Data
public class AutoPassRequest {

    private int passKind;

    private boolean forced;

    private int passTicketType;

    private String passTicket;

    private String storeCode;

    private String memberCode;

    private Long userId;


}
