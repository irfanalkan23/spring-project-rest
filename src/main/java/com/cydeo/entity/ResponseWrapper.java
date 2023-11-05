package com.cydeo.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter     //jackson using @Getter @Setter when converting JSON to Java obj
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)  //don't show if null. will not show "data" when we DELETE
public class ResponseWrapper {
//customizing response.

    private boolean success;
    private String message;
    private Integer code;
    private Object data;

    //constructor
    public ResponseWrapper(String message, Object data, HttpStatus httpStatus) {
        this.success = true;
        this.message = message;
//        this.code = HttpStatus.OK.value();
        this.code = httpStatus.value();
        this.data = data;
    }

    //constructor that we can use for delete
    public ResponseWrapper(String message, HttpStatus httpStatus) {
        this.message = message;
//        this.code = HttpStatus.OK.value();
        this.code = httpStatus.value();
        this.success = true;
    }
}