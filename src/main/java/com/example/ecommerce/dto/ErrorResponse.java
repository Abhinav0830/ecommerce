package com.example.ecommerce.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timeStamp;

    public ErrorResponse(int status,String message){
        this.status = status;
        this.message = message;
        this.timeStamp = LocalDateTime.now();
    }
}
