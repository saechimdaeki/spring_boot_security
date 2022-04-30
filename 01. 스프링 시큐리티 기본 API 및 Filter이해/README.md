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

## 07. 인증 API - Remember Me 인증
![image](https://user-images.githubusercontent.com/40031858/166084224-972521b0-b257-409a-b39e-a878cd26e481.png)

1. 세션이 만료되고 웹 브라우저가 종료된 후에도 어플리케이션이 사용자를 기억하는 기능
2. Remember-Me 쿠키에 대한 Http요청을 확인한 후 토큰 기반 인증을 사용해 유효성을 검사하고 토큰이 검증되면 사용자는 로그인 된다
3. 사용자 라이프 사이클
- 인증 성공(Remember-Me쿠키 설정)
- 인증 실패(쿠키가 존재하면 쿠키 무효화)
- 로그아웃(쿠키가 존재하면 쿠키 무효화)

```java
protected void configure(HttpSecurity http) throws Exception{
  http.rememberMe() // rememberMe기능이 작동함
      .rememberMeParameter("remember") // 기본 파라미터명은 remember-me
      .tokenValiditySeconds(3600) // Default는 14일
      .alwaysRemember(true) //리멤버 미 기능이 활성화되지 않아도 항상 실행
      .userDetailsService(userDetailsService)
}
```

## 08. 인증 API – RememberMeAuthenticationFilter
![image](https://user-images.githubusercontent.com/40031858/166084642-03949011-ec1b-4542-812c-611472a10921.png)

![image](https://user-images.githubusercontent.com/40031858/166084652-9ab5c33e-2264-4a43-91f1-7ecdb3556f90.png)

## 09. 인증 API - AnonymousAuthenticationFilter
![image](https://user-images.githubusercontent.com/40031858/166084869-061276ed-6603-47ed-ad84-2e85bc7e8c95.png)

- 익명사용자 인증 처리 필터
- 익명사용자와 인증 사용자를 구분해서 처리하기 위한 용도로 사용
- 화면에서 인증 여부를 구현할 때 isAnonymous()와 isAuthenticated()로 구분해서 사용
- 인증객체를 세션에 저장하지 않는다.

## 10. 인증 API - 동시 세션 제어 / 세션고정보호 / 세션 정책
### 인증 API - 동시 세션 제어 
![image](https://user-images.githubusercontent.com/40031858/166085142-18ac236c-7140-429c-ab38-555ce0492d74.png)

```java
protected void configure(HttpSecurity http) throws Exception{
  http.sessionManagement() // 세션 관리 기능 작동
      .maximumSessions(1) // 최대 허용 가능 세션 수 , -1 : 무제한 로그인 세션 허용
      .maxSessionsPreventsLogin(true) // 동시 로그인 차단함, false: 기존 세션 만료(default)
      .invalidSessionUrl("/invalid") // 세션이 유효하지 않을 때 이동 할 페이지
      .expiredUrl("/expired")  // 세션이 만료된 경우 이동 할 페이지
}

```

### 인증 API - 세션 고정 보호
![image](https://user-images.githubusercontent.com/40031858/166085222-a8e209f6-00bc-4b63-94b5-1322187d65af.png)

```java
protected void configure(HttpSecurity http) throws Exception{
  http.sessionManagement() // 세션 관리 기능 작동
      .sessionFixation().changeSessionId() //기본값 
      //none, migrateSession, newSession
}
```
### 인증 API - 세션 정책
```java
protected void configure(HttpSecurity http) throws Exception{
  http.sessionManagement() // 세션 관리 기능 작동
      .sessionCreationPolicy(SessionCreationPolicy.If_Required)
}
```
- `SessionCreationPolicy.Always` : 스프링 시큐리티가 항상 세션 생성
- `SessionCreationPolicy.If_Required` : 스프링 시큐리티가 필요 시 생성(기본갑)
- `SessionCreationPolicy.Never` : 스프링 시큐리티가 생성하지 않지만 이미 존재하면 사용
- `SessionCreationPolicy.Stateless` : 스프링 시큐리티가 생성하지 않고 존재해도 사용하지 않음

## 11. 인증 API - SessionManagementFilter ConcurrentSessionFilter
### 인증 API - SessionManagementFilter
1. 세션 관리
- 인증 시 사용자의 세션 정보를 등록, 조회, 삭제 등의 세션 이력을 관리  
2. 동시적 세션 제어
- 동일 계정으로 접속이 허용되는 최대 세션수를 제한
3. 세션 고정 보호
- 인증 할 때마다 세션쿠키를 새로 발급하여 공격자의 쿠키 조작을 방지
4. 세션 생성 정책
- Always, If_Required, Never , Stateless

### 인증 API - ConcurrentSessionFilter
- 매 요청 마다 현재 사용자의 세션 만료 여부 체크
- 세션이 만료로 설정되었을 경우 즉시 만료 처리
- Session.isExpired() == true
  - 로그 아웃 처리
  - 즉시 오류 페이지 응답
    - `"This Session has been expired"`

![image](https://user-images.githubusercontent.com/40031858/166092580-b2a3e18b-be6d-4486-ba86-7e2a03b5c932.png)

![image](https://user-images.githubusercontent.com/40031858/166092606-4876acee-0fef-41d1-a5ec-32f7693ed12c.png)

## 12. 인가 API - 권한 설정 및 표현식
### 인가 API - 권한 설정
- 선언적 방식
  - URL
    - http.antMatchers("/users/**").hasRole("USER")
  - Method
    - @PreAuthorize("hasRole('USER')")
      public void user(){//}
- 동적 방식 - DB 연동 프로그래밍
  - URL
  - Method

```java
@Override
protected void configure(HttpSecurity http) throws Exception{
  http
    .natMatcher("/shop/**")
    .authroizeRequests()
      .antMatchers("/shop/login", "/shop/users/**").permitAll()
      .antMatchers("/shop/mtpage").hasRole("USER")
      .antMatchers("/shop/admin/pay").access("harRole('ADMIN')")
      .antMatchers("/shop/admin/**").access("hasRole('ADMIN') or hasRole('SYS')")
      .anyRequest().authenticated()
}
// 주의 사항 - 설정 시 구체적인 경로가 먼저오고 그것보다 큰 범위의 경로가 뒤에 오도록 해야한다.
```

### 인가 API - 표현식
|||
|:--:|:--:|
|`메소드`|`동작`|
|`authenticated()`|인증된 사용자의 접근을 허용|
|`fullyAuthenticated()`|인증된 사용자의 접근을 허용, rememberMe 인증 제외|
|`permitAll()`|무조건 접근을 허용|
|`denyAll()`|무조건 접근을 허용하지 않음|
|`anonymous()`|익명사용자의 접근을 허용|
|`rememberMe()`|기억하기를 통해 인증된 사용자의 접근을 허용|
|`access(String)`|주어진 SpEL 표현식의 평가 결과가 true이면 접근을 허용|
|`hasRole(String)`|사용자가 주어진 역할이 있다면 접근을 허용|
|`hasAuthority(String)`|사용자가 주어진 권한이 있다면 접근을 허용|
|`hasAnyRole(String...)`|사용자가 주어진 권한이 있다면 접근을 허용|
|`hasAnyAuthority(String...)`|사용자가 주어진 권한 중 어떤 것이라도 있다면 접근을 허용|
|`hasIpAddress(String)`|주어진 IP로부터 요청이 왔다면 접근을 허용|

## 13. 인증/ 인가 API - ExceptionTranslationFilter, RequestCacheAwareFilter
### 인증/인가 API - ExceptionTranslationFilter
- `AuthenticationException`
  - 인증 예외 처리
    1. AuthenticationEntryPoint 호출
       - 로그인 페이지 이동, 401 오류 코드 전달 등
    2. 인증 예외가 발생하기 전의 요청 정보를 저장
       - RequestCache - 사용자의 이전 요청 정보를 세션에 저장하고 이를 꺼내 오는 캐시 메커니즘
         - SavedRequest - 사용자가 요청했던 request 파라미터 값들, 그 당시의 헤더값들 등이 저장
- `AccessDeniedException`
  - 인가 예외 처리
    - AccessDeniedHandler 에서 예외 처리하도록 제공

![image](https://user-images.githubusercontent.com/40031858/166099607-19d40555-c37a-4319-8319-55d0f848bc4f.png)

```java
protected void configure(HttpSecurity http) throws Exception{
  http.excptionHandling()
      .authenticationEntryPoint(authenticationEntryPoint()) // 인증실패 시 처리
      .accessDeniedHandler(accessDeniedHandler()) // 인증실패 시 처리
}
```
### 인증/인가 API - ExceptionTranslationFilter
![image](https://user-images.githubusercontent.com/40031858/166099679-a40375b0-e1ec-45c4-b913-afd335e61468.png)

## 14. Form 인증 - CSRF, CsrfFilter
### Form 인증 - CSRF (사이트 간 요청 위죠)
![image](https://user-images.githubusercontent.com/40031858/166100400-c038d73f-0521-4aa1-aa66-7652ea9aae0c.png)

### Form 인증 - CsrfFilter
- 모든 요청에 랜덤하게 생성된 토큰을 HTTP 파라미터로 요구
- 요청 시 전달되는 토큰 값과 서버에 저장된 실제 값과 비교한 후 만약 일치하지 않으면 요청은 실패한다

- `Client`
  - < input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  - HTTP 메소드 : PATCH , POST , PUT, DELETE
- `Spring Security`
  - http.csrf() : 기본 활성화되어 있음
  - http.csrf().disabled() : 비활성화