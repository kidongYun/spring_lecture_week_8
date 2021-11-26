# 7주차 강의노트

영속화에 대한 이야기
- 영속화 벤더로 대표적으로 사용되는 mysql, redis, elasticsearch 를 Spring 에서 사용하는 방법을 알아보자.
- 각 벤더 별 특징들을 모두 보는 것은 이 주제에 벗어나기 때문에 최대한 간단하게 연동에 포커스를 맞추어보자.

## docker

- 각 영속화 벤더 서비스들을 간단하게 설치하기 위해 docker 기반으로 설치를 한다
- docker 라는 것은 컨테이너 기반으로 서비스를 개발할 수 있게 도와주는 프로그램
- 컨테이너에는 우리가 만든 어플리케이션 서비스 뿐만 아니라 거기에 필요한 환경 설정 정보들도 포함한다.
- docker 를 사용하게 되면 호스트 서버의 시스템 환경과 격리된 컨테이너에서 서비스가 동작되기 때문에 시스템과의 의존성이 사라진다.
- docker image 라는 것을 활용하면 이미 만들어진 컨테이너 들을 손쉽게 사용할 수 있다.
- 여기서는 다른 개발자들이 이미 작업해둔 image 들을 가져와서 설정할 예정

## 간단한 docker 명령어

- docker 를 활용해 mysql image 를 가져오자

```
> docker pull mysql
```

- 로컬에 다운받아진 docker image 를 확인해보자

```
> docker images
```

- 다운받은 docker image 를 container 생성
- run 명령어를 치게되면 container 가 생성됨과 동시에 container 가 실행이 된다.

```
> docker run --name mysql-container -e MYSQL_ROOT_PASSWORD=artineer -d -p 3306:3306 mysql:latest
```

- --name 속성은 컨테이너의 이름을 지정한다
- -e 속성은 그 컨테이너 안에서 사용되는 환경 변수 값을 전달할 수 있다.
- -d 속성은 해당 컨테이너가 daemon 형태로 실행되도록 할 수 있다. (background)
- -p 속성은 호스트와 컨테이너간의 포트를 지정한다. 여기서는 호스트의 3306 포트와 컨테이너의 3306 포트를 매핑한다.
- 마지막 mysql:latest 는 사용할 docker image 이름을 지정

- 로컬에 구성된 docker container 목록을 확인

```
> docker ps   // 실행되고 있는 container 만 조회

> docker ps -a // 로컬에 구성된 모든 container 조회
```

- 구성된 docker 컨테이너를 제거

```
> docker rm mysql-container
```

- 실행되고 있는 docker container 종료

```
> docker stop mysql-container
```

- 종료된 docker container 시작

```
> docker start mysql-container
```

- 실행되고 있는 container 의 shell 프로그램 실행
- 여기서는 mysql-container 에 접속한다고 해석해도 무방

```
docker exec -it mysql-container bash
```
mysql
- Mysql 접속

```
> mysql -u root -p
Enter password : <pawssword>
```

## mysql

- 데이터베이스 추가

```
> CREATE DATABASE artineer
```

mysql 서버에 대한 정보를 연동을 위해 application.properties 에 기입

```
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/artineer?serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=artineer

spring.jpa.show-sql=true
spring.jpa.generate-ddl=true
spring.jpa.database=mysql
spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect
```

## mysql 한글 깨짐 해결

- mysql 한글 깨짐 문제를 해결하려면 /etc/mysql/my.cnf 환경설정 파일을 수정해야 한다.
- container 안에는 vi 에디터가 존재하지 않는다. 설치를 위해 apt-get 활용하여 설치를 진행한다.

```
> apt-get update
> apt-get install vim
```

- vi 를 활용해서 아래 설정 내용 추가
  vi
```
[client]
default-character-set=utf8

[mysql]
default-character-set=utf8

[mysqld]
pid-file        = /var/run/mysqld/mysqld.pid
socket          = /var/run/mysqld/mysqld.sock
datadir         = /var/lib/mysql
secure-file-priv= NULL
collation-server = utf8_unicode_ci
init-connect='SET NAMES utf8'
character-set-server = utf8
```

- 데이터베이스 character set 설정 변경
-
```
> ALTER DATABASE artineer DEFAULT CHARACTER SET utf8;
```

## redis

- key-value 기반 형태로 데이터를 저장하는 인메모리 저장소
- 일반적으로 캐싱의 목적으로 사용되며 session 저장소로도 많이 애용되고 있다.
- NoSQL 저장소이기 때문에 빠르고 인 메모리 기반이기 때문에 빠르다.

- redis 관련된 docker image 를 로컬로 다운 받는다.

```
> docker pull redis
```

- redis image 활용하여 container 를 실행하자.

```
> docker run -p 6379:6379 --name redis_boot -d redis
```

- redis-cli 접속

```
> docker exec -i -t redis_boot redis-cli
```

### redis 간단한 명령어

- 아래 명령어를 사용하면 등록되어 있는 key-value 값을 모두 가져온다.
- 실무에서는 사용을 지양하는 명령어

```
> keys *
```

- 문자열 타입의 정보 저장하기

```
> set KEY1 "VALUE1"
```

- 저장된 value 값을 key 값 기준으로 확인하기

```
> get KEY1
```

### 스프링에서 redis 설정

- build.gradle 에서 레디스 의존성 주입하자

```groovy
	implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```

- application.properties 에서 레디스 설정정보 추가

```
# redis 설정
spring.redis.host=127.0.0.1
spring.redis.port=6379
```

- 스프링에서는 redis 를 스프링에서 손쉽게 사용하기 위해 RedisTemplate 을 제공한다.
- RedisTemplate 객체를 빈으로 등록

```java
@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}
```

- RedisTemplate 활용해서 간다하게 String 타입의 데이터들을 넣고 가져와보자.

```java
@Slf4j
@Component
public class RedisRunner implements ApplicationRunner {
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisRunner(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        redisTemplate.opsForValue().set("YUN", "KIDONG");
        log.info(redisTemplate.opsForValue().get("YUN"));
    }
}
```

- 실제로는 String type 의 Key-value 데이터를 넣는 케이스가 많기 때문에 스프링에서는 String 타입에 특화된 StringRedisTemplate 를 제공한다.

```java
@Configuration
public class RedisConfig {
    
    // ...
    
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        return stringRedisTemplate;
    }
}
```

```java
@Slf4j
@Component
public class RedisRunner implements ApplicationRunner {
    private final RedisTemplate<String, String> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisRunner(RedisTemplate<String, String> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        redisTemplate.opsForValue().set("key1", "value1");
        log.info(redisTemplate.opsForValue().get("key1"));

        stringRedisTemplate.opsForValue().set("key2", "value2");
        log.info(stringRedisTemplate.opsForValue().get("key2"));
    }
}
```

- RedisTemplate 객체를 사용하지 않고 Redis 를 Repository 처럼 사용이 가능하다.
- @Entity -> @RedisHash 으로 변경
- RDB 들은 Repository 인터페이스로 추상화되어 동일 코드로 각 벤더에 처리가 가능하지만
- NoSQL 은 각자의 특징들이 너무 달라서 아직 그런 기능은 존재하지 않는다.

```java
// ...
@RedisHash
public class Article {
    // ...
}
```

- API 요청을 통해 실제로 repository 코드가 정상적으로 동작하는지 확인하자.

redis-cli 에서 객체형태로 들어간 데이터도 확인이 가능하다.

```
> hgetall <key>

> hget <key> <field>
```

## 7주차 강의

- https://youtu.be/zOQZ1BYnrMU