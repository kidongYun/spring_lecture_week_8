package com.artineer.spring_lecture_week_2.conroller;

import com.artineer.spring_lecture_week_2.apiversion.ApiVersion;
import com.artineer.spring_lecture_week_2.domain.Article;
import com.artineer.spring_lecture_week_2.dto.ArticleDto;
import com.artineer.spring_lecture_week_2.dto.Response;
import com.artineer.spring_lecture_week_2.service.ArticleService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@ApiVersion
@RequiredArgsConstructor
@RequestMapping("/api/v*/article")
@RestController
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public Response<Long> post(@RequestBody @Valid ArticleDto.ReqPost request) {
        return Response.ok(articleService.save(Article.of(request)));
    }


    /**
     * @api {get} /api/v1/article/{id} Article 조회
     * @apiGroup article
     * @apiVersion 1.0.0
     * @apiPermission User
     * @apiDescription created at 2021-12-10 1:11
     *
     * @apiParam {Long} id article ID 값
     * @apiParamExample {json} Request (example):
     * {
     *     "id": "12"
     * }
     *
     * @apiSuccess {ArticleDto.Res} ArticleDto.Res 성공 시 조회된 값
     * @apiSuccessExample {json} Response (example):
     * {
     *     "id": "12",
     *     "title": "아티니어",
     *     "content": "아티니어 강의 많관부"
     * }
     **/
    @ApiOperation(value = "Article 정보 조회", notes = "{id} 에 해당하는 Article 정보를 조회합니다.")
    @ApiImplicitParam(name = "id", value = "Article ID 입니다", required = true, dataType = "string", defaultValue = "None")
    @ApiResponse(code = 200, message = "성공입니다.")
    @GetMapping("/{id}")
    public Response<ArticleDto.Res> get(@PathVariable Long id) {
        return Response.ok(ArticleDto.Res.of(articleService.findById(id)));
    }

    @PutMapping("/{id}")
    public Response<Object> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
        return Response.ok(ArticleDto.Res.of(articleService.update(Article.of(request, id))));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Response.ok();
    }
}
