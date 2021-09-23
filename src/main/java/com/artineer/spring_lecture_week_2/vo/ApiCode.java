package com.artineer.spring_lecture_week_2.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApiCode {
    /* COMMON */
    SUCCESS("CM0000", "정상입니다")
    ;

    private final String name;
    private final String desc;

    ApiCode(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}
