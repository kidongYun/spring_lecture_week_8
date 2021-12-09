package com.artineer.spring_lecture_week_2.conroller.v2;

import com.artineer.spring_lecture_week_2.apiversion.ApiVersion;
import com.artineer.spring_lecture_week_2.dto.ArticleDto;
import com.artineer.spring_lecture_week_2.dto.Response;
import com.artineer.spring_lecture_week_2.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiVersion(2)
@RequiredArgsConstructor
@RequestMapping("/api/v*/article")
@RestController
public class ArticleV2Controller {
    private final ArticleService articleService;

    @GetMapping(value = "/{id}")
    public Response<ArticleDto.ResV2> get(@PathVariable Long id) {
        return Response.ok(ArticleDto.ResV2.of(articleService.findById(id)));
    }
}
