# 8주차 강의노트

## Swagger

- API 사용 방법을 알게해주는 문서를 만드는 것은 실제로 필수적인 업무.
- 수작업으로 API 문서를 만들게 되면 해야할 일이 배로 늘어나며, API 수정이 일어났을 때 문서도 지속 관리해야하는 이슈가 있다.
- Swagger 를 활용해서 API 문서를 자동으로 만들어 보자.

- Swagger 사용하기 위해서 의존성을 주입하자.

```groovy
    // ...

	implementation 'io.springfox:springfox-swagger2:2.9.2'
	implementation 'io.springfox:springfox-swagger-ui:2.9.2'

    // ...
```

- Swagger API 문서 자동화를 하기 위해 핵심적으로 사용되는 Docket 객체를 빈으로 등록하자.
- Docket 객체가 Swagger 에 가장 기본이 되며, 다양한 설정을 이 객체를 통해 진행할 수 있다.

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2);
    }
}
```

- 서버를 띄운 뒤 아래 경로에서 자동으로 생성된 Swagger API 문서를 확인할 수 있다.
- 구현되어 있는 모든 API endpoint 를 확인할 수 있다.

```
http://127.0.0.1:8080/swagger-ui.html
```

## 원하는 API 만 문서화 하기.

- 위 Swagger 문서에 접속해보면 모든 API 에 대해서 문서가 생성된 것을 확인할 수 있다.
- ApiSelector 를 활용하면 원하는 API 만 문서화 할 수 있도록 설정할 수 있다.

```java
public class SwaggerConfig {
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())    // 특정 API 만을 선택할 수 있음, 여기서는 모든 API 를 선택
                .paths(PathSelectors.any())     // 특정 path 의 API 를 선택할 수 있음, 여기서는 모든 path 를 선택
                .build();
    }
}
```

- RequestHandlerSelectors 가 제공하는 함수들은 간단하게 아래와 같다.

<hr/>

- RequestHandlerSelectors.any() : 모든 API 를 선택
- RequestHandlerSelectors.none() : 아무 API 도 선택하지 않음
- RequestHandlerSelectors.basePackage("...") : 해당 package 포함되는 API 만 선택
- ...

<hr/>

- PathSelectors 가 제공하는 함수들은 간단하게 아래와 같다.

<hr/>

- PathSelectors.any() : 모든 경로의 API 를 선택
- PathSelectors.none() : 아무 경로의 API 도 선택하지 않음
- PathSelectors.ant() : ant() 경로에 포함되는 API 만 선택
- PathSelectors.regex() : regex() 정규식에 포함되는 API 만 선택

<hr/>

## 문서에 기본적인 설명 추가하기

- Swagger API 문서에 기본적인 설명 정보들을 추가할 때에는 ApiInfo 객체를 활용하면 좋다.

```java
public class SwaggerConfig {
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                // ...
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .contact(new Contact("name", "url", "email"))
                .title("It's title")
                .description("It's description")
                .version("It's version")
                .license("license")
                .build();
    }
}
```

## 각 API 별 부가 설명 정보 추가하기

- 한 API 에 대해서 제목과 설명글을 @ApiOperation 어노테이션을 통해 등록할 수 있다.

```java
public class ArticleController {
    // ...
    
    @ApiOperation(value = "Article 정보 조회", notes = "{id} 에 해당하는 Article 정보를 조회합니다.")
    public Response<ArticleDto.Res> get(@PathVariable Long id) {
        // ...
    }
    
    // ...
}
```

- 각 파라미터에 대해 정보를 넣어야 한다면 @ApiImplicitParam 어노테이션을 사용하면 된다.
- 여러 개를 적용해야 할 경우에는 @ApiImplicitParams 처럼 복수형 어노테이션도 있다.

```java
public class ArticleController {
    // ..

    @ApiOperation(value = "Article 정보 조회", notes = "{id} 에 해당하는 Article 정보를 조회합니다.")
    @ApiImplicitParam(name = "id", value = "article id", required = true, dataType = "string", defaultValue = "None")
    public Response<ArticleDto.Res> get(@PathVariable Long id) {
        // ...
    }
    
    // ...
}
```

- 응답 값에 대해서 설명이 필요하다면 @ApiResponse 어노테이션을 사용하자.
- HTTP STATUS CODE 에 따라서 응답 결과를 구분해서 처리할 수 있다.

```java
public class ArticleController {
    // ...
    
    @ApiOperation(value = "Article 정보 조회", notes = "{id} 에 해당하는 Article 정보를 조회합니다.")
    @ApiImplicitParam(name = "id", value = "article id", required = true, dataType = "string", defaultValue = "None")
    @ApiResponse(code = 200, message = "성공입니다.")
    public Response<ArticleDto.Res> get(@PathVariable Long id) {
        // ...
    }
    
    // ...
}
```

- DTO 객체에 각 필드 별 예제 정보를 넣어야 할 때에는 @ApiModelProperty 어노테이션을 사용하자.

```java
public class ArticleDto {
    // ...
    public static class ReqPost {
        @ApiModelProperty(name = "title", example = "gildong")
        @NotBlank
        String title;
        // ...
    }
    
    // ...
}
```

## APIDOC

- apidoc 은 node 기반으로 구현된 서비스이다. 그렇기 때문에 npm 패키지 매니저를 통해 설치가 가능하다
- npm 은 node 를 설치하게 되면 보통 함께 설치가 되며 맥의 경우 homebrew 를 통해 간단하게 설치가 가능하다.

```
> brew install node
```

```
> npm install apidoc -g
```

- 권한 이슈가 발생한 다면 sudo 명령어를 추가해서 설치하자.

- apidoc 를 활용하면 자동으로 api 문서를 html 형태로 만들어 준다.
- 이를 위해 기본적인 설정 정보를 넣어줘야 한다. 루트 디렉토리에 apidoc.json 파일을 만들고 설정 정보를 넣자.

```json
{
  "name": "아티니어 스프링 강의 APIDOC 문서",
  "version": "1.0.0",
  "description": "아티니어 스프링 강의 8주차에서 진행된 API 문서 자동화 내용입니다.",
  "title": "Api Documentation",
  "url": "http://api.artineer.com/v1"
}
```

- apidoc 은 주석을 기반으로 동작한다.
- 보통 컨트롤러 상단에 각 API를 설명하는 주석을 달고 apidoc 은 이 주석을 보고 문서를 만들어 준다.
- article 조회하는 api 를 기준으로 주석을 추가해보자.

```java
// ...
public class ArticleController {
    // ...

    /**
     * @api {get} /api/v1/article/{id} Article 조회
     * @apiName available
     * @apiGroup article
     * @apiVersion 1.0.0
     * @apiPermission User
     * @apiDescription created at 2021-12-03 14:00
     **/
    // ...
    public Response<ArticleDto.Res> get(@PathVariable Long id) {
        // ...
    }
    
    // ...
}
```

- apidoc 문서를 생성할 때에는 어떤 파일들을 생성할 것인지, 결과물은 어디에 저장할 것인지 즉 입력과 출력을 파라미터로 넣어주어야 한다.
- -i 옵션과 디렉토리를 넣어주면 하위에 있는 파일들을 입력한다는 의미이고, -o 옵션은 출력할 결과물을 어디에 저장할 것인지를 넣어야 한다.

```
> apidoc -i .\src\main\ -o .\build\apidoc
```

- -o 옵션에 넣어준 출력 디렉토리에 있는 index.html 파일을 실행하면 문서를 확인할 수 있다.
- 해당 파일을 웹서버를 띄워 다른 곳에서도 접근이 하도록 하여 사용한다.

```java
/**
     * @api {get} /api/v1/article/{id} Article 조회
     * @apiName available
     * @apiGroup article
     * @apiVersion 1.0.0
     * @apiPermission User
     * @apiDescription created at 2021-12-03 14:00
     *
     * @apiParam {Long} id article ID 값
     * @apiParamExample {json} Request (example):
     * {
     *     "id": "12"
     * }
     *
     * @apiSuccess {ArticleDto.Res} ArticleDto.Res 성공시 조회된 값
     * @apiSuccessExample {json} Response (example):
     * {
     *     "id": "12",
     *     "title": "아티니어",
     *     "content": "아티니어 강의 많관부"
     * }
     **/
```

- @apiParam, @apiSuccess 필요한 속성을 찾아서 위처럼 넣어주면 해당 속성에 맞는 정보들이 apidoc 에 그려진다.

## API version 처리

- 버전을 구분하기 위해서 아래 3가지 방법이 있다.
  - URI 에 명시하는 방법
  - Request QueryString 에 명시하는 방법
  - Header 에 명시하는 방법

- V2 필요성을 만들기 위해 ARTICLE 을 조회하는 GET API 를 사용할 때 id 값을 생략되는 API 가 필요하다고 생각해보자.
- 기존 V1 API 에서 id 값을 지우게 되면 다른 어느 곳에서 V1 API 의 id 값을 사용하는 곳이 있는지 모르기 때문에 하위 호환성이 지켜지지 않는다.
- 이럴 때에는 새로운 버전을 만들어서 처리한다.

```java
@RequiredArgsConstructor
@RequestMapping("/api/v2/article")
@RestController
public class ArticleV2Controller {
    private final ArticleService articleService;

    @GetMapping("/{id}")
    public Response<ArticleDto.ResV2> get(@PathVariable Long id) {
        return Response.ok(ArticleDto.ResV2.of(articleService.findById(id)));
    }
}
```

```java
public class ArticleDto {
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
```

- 만약 서비스에서 도메인 중심으로 다루지 않고 Res 와 같은 DTO 객체에 종속되도록 만들었다면 서비스도 V2 객체가 새로 필요했을 것이다.

```java
public class ArticleV2Controller {
    // ...
    @GetMapping(value = "/{id}", params = "version=2")
    public Response<ArticleDto.ResV2> get(@PathVariable Long id) {
        return Response.ok(ArticleDto.ResV2.of(articleService.findById(id)));
    }
}
```

```java
public class ArticleV2Controller {
    // ...
    @GetMapping(value = "/{id}", headers = "X-API-VERSION=2")
    public Response<ArticleDto.ResV2> get(@PathVariable Long id) {
        return Response.ok(ArticleDto.ResV2.of(articleService.findById(id)));
    }
}
```

- 위 방법은 URI 가 아닌 query string 이나 header 방식으로 API 버전 분기하는 처리를 보여준다.
- 여기서는 사용하지 않을 예정이니 간단하게 보기만하고 넘어가자.

## 버전에 따른

## Optional) API 상위 버전이 없는 경우, 하위 버전으로 자동 매핑

- 현재 버전분기 처리에서는 POST article v2 를 날리게 되면 없는 API 라고 발생할 것이다.
- 버전분기된 소스가 많아질수록 특정 API의 버전이 어디까지 나와있는지, 어떻게 처리되고 있는지 알기가 어렵다.
- 상위버전의 API 가 없다면 하위버전으로 자동 매핑되고, 맞는 버전이 있다면 해당 버전으로 매핑되도록 구현해보자.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ApiVersion {
    int value() default 1;
}
```

- 버전 명시를 위해 @ApiVersion 어노테이션을 만들었다.
- 두번째는 버전을 표현하는 도메인 객체를 하나 만들자.

```java
public class Version implements Comparable<Version> {
    public static final int MAX_VERSION = 9999999;

    private final int version;

    public Version(int version) {
        this.version = version;
    }

    @Override
    public int compareTo(Version other) {
        return Integer.compare(this.version, other.version);
    }
}
```

- 여기서 구현하는 버전 처리는. 특정 범위를 가지고 있고, 범위에 포함되었을 때 API 가 지원되도록 할 것이다.
- 버전범위를 표현하는 객체를 하나 만들자.

```java
public class VersionRange {
    private Version from;
    private Version to;

    public VersionRange(int from, int to) {
        this.from = new Version(from);
        this.to = new Version(to);
    }

    public boolean includes(int other) {
        Version otherVersion = new Version(other);

        int fromCondition = from.compareTo(otherVersion);
        int toCondition = to.compareTo(otherVersion);

        if(fromCondition <= 0 && toCondition >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public int compareTo(VersionRange other) {
        return this.from.compareTo(other.from);
    }
}
```

- 클라이언트의 요청정보(URL 등)와 컨트롤러와 연결해주는 작업은 HandlerMapping 이라는 인터페이스를 구현한다.
- 다양한 구현체들이 있지만 현대 대부분 스프링은 RequestMappingHandlerMapping 을 사용하고 있다.
- 우리는 여기에 ApiVersion 에 따라서 다르게 매핑해주기 위해 이를 커스터마이징 할 것이다.

```java
public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        ApiVersion typeAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
        return createCondition(typeAnnotation);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        ApiVersion methodAnnotation = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        return createCondition(methodAnnotation);
    }

    private RequestCondition<?> createCondition(ApiVersion apiVersion) {
        if (apiVersion == null) {
            return null;
        }

        return new ApiVersionRequestCondition(apiVersion.value(), Version.MAX_VERSION);
    }
}
```

```java
public class ApiVersionRequestCondition extends AbstractRequestCondition<ApiVersionRequestCondition> {

    private final Set<VersionRange> versions;

    public ApiVersionRequestCondition(int from, int to) {
        this(versionRange(from, to));
    }

    public ApiVersionRequestCondition(Collection<VersionRange> versions) {
        this.versions = Set.copyOf(versions);
    }

    private static Set<VersionRange> versionRange(int from, int to) {
        HashSet<VersionRange> versionRanges = new HashSet<>();

        if(from > 0) {
            int toVersion = (to > 1) ? to : Version.MAX_VERSION;
            VersionRange versionRange = new VersionRange(from, toVersion);

            versionRanges.add(versionRange);
        }

        return versionRanges;
    }

    @Override
    public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
        log.debug("version combining: {} + {}", this, other);
        Set<VersionRange> newVersions = new LinkedHashSet<>(this.versions);
        newVersions.addAll(other.versions);

        return new ApiVersionRequestCondition(newVersions);
    }

    @Override
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        String accept = request.getRequestURI();

        Pattern regexPattern = Pattern.compile("(\\/api\\/v)(\\d+)(\\/).*");

        Matcher matcher = regexPattern.matcher(accept);
        if(matcher.matches()) {
            int version = Integer.parseInt(matcher.group(2));

            for(VersionRange versionRange : versions) {
                if(versionRange.includes(version)) {
                    return this;
                }
            }
        }

        return null;
    }

    @Override
    public int compareTo(ApiVersionRequestCondition other, HttpServletRequest request) {

        if(versions.size() == 1 && other.versions.size() == 1) {
            return versions.stream().findFirst().get().compareTo(other.versions.stream().findFirst().get()) * -1;
        }

        return 0;
    }

    @Override
    protected Collection<?> getContent() {
        return versions;
    }

    @Override
    protected String getToStringInfix() {
        return " && ";
    }
}
```

## 8주차 영상

- https://youtu.be/TU_P_lYG8uk