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

## 03. 인증 API - HTTP Basic 인증, BasicAuthenticationFilter
### 인증 API - HTTP Basic 인증
![image](https://user-images.githubusercontent.com/40031858/165903185-12e6a119-14ff-4ba3-8465-60f87ef04e74.png)

- HTTP는 자체적인 인증 관련 기능을 제공하며 HTTP 표준에 정의된 가장 간단한 인증 기법이다
- 간단한 설정과 Stateless가 장점 - Session Cookie(JSESSIONID) 사용하지 않음
- 보호자원 접근시 서버가 클라이언트에게 401 Unauthorized 응답과 함께 WWW-Authenticate header를 기술해서 인증요구를 보냄
- Client는 ID:Password 값을 Base64로 Encoding한 문자열을 Authorization Header에 추가한 뒤 Server에게 Resource를 요청
  - Autorization: Basic cmVzdDpyZXN0
- ID,Password가 Base64로 Encoding되어 있어 ID, Password가 외부에 쉽게 노출되는 구조이기 때문에 SSL이나 TLS는 필수이다

```java
protected void configure(HttpSecurity http) throws Exception{
  http.httpBasic();
}
```
### 인증 API - BasicAuthentiacionFilter
![image](https://user-images.githubusercontent.com/40031858/165903650-6392f156-f9b6-462f-907e-21a2cb92b068.png)

## 04. 인증 API - Form 인증
### 인증 API - Form 인증

![image](https://user-images.githubusercontent.com/40031858/165903796-21f75fd8-0645-4261-9a36-71dbbd4419df.png)

```java
protected void configue(HttpSecurity http) throws Exception {
  http.formLogin() // Form 로그인 인증 기능이 작동
      .loginPalge("/login.html") // 사용자 정의 로그인 페이지
      .defaultSuccessUrl("/home") // 로그인 성공 후 이동 페이지
      .failureUrl("/login.html?error=true") // 로그인 실패 후 이동 페이지
     .usernameParameter("username") // 아이디 파라미터명 설정
     .passwordParameter("password") // 패스워드 파라미터명 설정
     .loginProcessingUrl("/login") // 로그인 Form Action Url 
     .successHandler(loginSuccessHandler()) // 로그인 성공 후 핸들러
     .failureHandler(loginFailureHandler())  // 로그인 실패 후 핸들러
}
```

## 05. 인증 API - UsernamePasswordAuthenticationFilter
### 인증 API - Login Form 인증. 
![image](https://user-images.githubusercontent.com/40031858/165906048-25aa9951-824c-463a-8fe3-32d47a4af935.png)

### 인증 API - UsernamePasswordAuthenticationFilter
![image](https://user-images.githubusercontent.com/40031858/165906151-eb4fc3da-039e-4e1b-bdd3-9fb8154af027.png)

## 06. 인증 API - Logout, LogoutFilter
### 인증 API - Logout
![image](https://user-images.githubusercontent.com/40031858/165908238-7a1e079d-59f6-42b1-a769-1a99adc3a7e1.png)

```java
protected void configure(HttpSecurity http) throws Exception{
  http.logout() // 로그아웃 기능이 작동함
      .logoutUrl("/logout") //로그아웃 처리 URL
      .logoutSuccessUrl("/login")
      .deleteCookies("JSESSIONID", "remember-me") // 로그아웃 후 쿠키삭제
      .addLogoutHandler(logoutHandler()) //로그아웃 핸들러
      .lougoutSuccessHandler(logoutSuccessHandler()) //로그아웃 성공 후 핸들러 
}
```

### 인증 API - Logout
![image](https://user-images.githubusercontent.com/40031858/165908574-944c7e77-1687-4b1d-bf82-295e2a9a05f3.png)

### 인증 API - LogoutFilter
![image](https://user-images.githubusercontent.com/40031858/165908938-c746dc44-d18c-4a20-b635-e73e14950529.png)