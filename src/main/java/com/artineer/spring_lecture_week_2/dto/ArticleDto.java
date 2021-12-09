package com.artineer.spring_lecture_week_2.dto;

import com.artineer.spring_lecture_week_2.domain.Article;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class ArticleDto {
    @Getter
    @Builder
    public static class ReqPost {
        @ApiModelProperty(name = "title", example = "gildong")
        @NotBlank
        String title;
        @NotBlank
        String content;
    }

    @Getter
    public static class ReqPut {
        String title;
        String content;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res {
        private String id;
        private String title;
        private String content;

        public static Res of(Article from) {
            return Res.builder()
                    .id(String.valueOf(from.getId()))
                    .title(from.getTitle())
                    .content(from.getContent())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResV2 {
        private String title;
        private String content;

        public static ResV2 of(Article from) {
            return ResV2.builder()
                    .title(from.getTitle())
                    .content(from.getContent())
                    .build();
        }
    }
}
