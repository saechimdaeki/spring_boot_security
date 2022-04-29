# 스프링 시큐리티 기본 API 및 Filter 이해

## 01. 인증 API - 스프링 시큐리티 의존성 추가

- 스프링 시큐리티의 의존성 추가 시 일어나는 일들
  - 서버가 기동되면 스프링 시큐리티의 초기화 작업 및 보안 설정이 이루어진다
  - 별도의 설정이나 구현을 하지 않아도 기본적인 웹 보안 기능이 현재 시스템에 연동되어 작동함
  1. 모든 요청은 인증이 되어야 자원에 접근이 가능하다
  2. 인증 방식은 폼 로그인 방식과 httpBasic 로그인 방식을 제공하낟
  3. 기본 로그인 페이지 제공한다
  4. 기본 계정 한 개 제공한다 - username: user / password: 랜덤 문자열
- 문제점
  - 계정 추가, 권한 추가, DB 연동 등
  - 기본적인 보안 기능 외에 시스템에서 필요로 하는 더 세부적이고 추가적인 보안기능이 필요

## 02. 인증 API - 사용자 정의 보안 기능 구현 
![image](https://user-images.githubusercontent.com/40031858/165889149-6954f674-766d-4180-af7e-6720505f5231.png)

### 인증 API - SecurityConfig 설정
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
            .authorizeRequests()
            .anyRequest().authenticated()
        .and()
            .formLogin();
    }
}

```

