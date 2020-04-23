package com.example.demo.security;

import com.example.demo.auth.ApplicationUserService;
import com.example.demo.jwt.JwtConfig;
import com.example.demo.jwt.JwtSecretKey;
import com.example.demo.jwt.JwtTokenVerifyFilter;
import com.example.demo.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

import static com.example.demo.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, ApplicationUserService applicationUserService,
                                     JwtSecretKey jwtSecretKey,
                                     JwtConfig jwtConfig) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
        this.secretKey = jwtSecretKey.getSecretKeyForSigning();
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //.csrf().disable() // When to use csrf - https://docs.spring.io/spring-security/site/docs/3.2.0.CI-SNAPSHOT/reference/html/csrf.html
                // Below line will ensure that you can hit the apis with other clients than browser, for example postman etc.,
                //You will get a csrf cookie as part of the response object from the GET request at the client end. Copy that and set it as a header on the server end for post, delete, put requests
                //.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //Jwts are stateless. Hence we need to mention this so that the sessions are not stored
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, secretKey))
                .addFilterAfter(new JwtTokenVerifyFilter(secretKey, jwtConfig), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers( "/", "index", "/css/*", "/js/*") // "/" is the root page
                .permitAll()// this makes sure we are whitelisting all the pages which is mentioned in the antmatchers
                .antMatchers("/api/**")
                .hasRole(STUDENT.name())// This will make sure all the endpoints starting with /api is accessible to only the ones with Student role
                //the below line is the alternate way to user ant matchers, where we are providing the permissions to the roles based for the http methods
                //NOTE: Order of defining the antmatchers matter. Any change in order might lead to giving permissions to all the apis and to all the roles
                // Below lines have been commented and the @PreAuthorize annotation has been added to StudentManagementController.
                // These below lines can be uncommented and @PreAuthorize annotation can be remoted
                // Also, as part of preauthorize annotation, @EnableGlobalMethodSecurity(prePostEnabled = true) needs to be set
                /*.antMatchers(HttpMethod.DELETE, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission()) // Anyone who has course_write permission can use the delete method
                .antMatchers(HttpMethod.POST, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.PUT, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, "/management/api/**").hasAuthority(STUDENT_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, "/management/api/**").hasAuthority(STUDENT_WRITE.getPermission())
                .antMatchers(HttpMethod.PUT, "/management/api/**").hasAuthority(STUDENT_WRITE.getPermission())
                .antMatchers(HttpMethod.GET, "/management/api/**").hasAnyRole(ADMIN.name(), ADMINTRAINEE.name())*/
                .anyRequest()
                .authenticated();
                //.and()
                //JWt authentication does not require the below set of lines
                //.formLogin()
                    //.loginPage("/login")
                    //.permitAll() // Allow the "login.html" page to be accessed by all
                    //.defaultSuccessUrl("/courses", true) // This is the default landing page(courses.html) after successfull login
                    //.usernameParameter("username")// This field has to match with the "name" parameter of the login.html file's username attribute
                    //.passwordParameter("password")// This field has to match with the "name" parameter of the login.html file's password attribute
                //.and()
                //Here while dealing with sessions(NOT JWT), sessions have default expiration of 30min. rememberMe()[default = 2 weeks] helps increase that expiration time.
                //rememberMe cookie contains username, expiration and md5 hash of username & expiration
                //.rememberMe();
                //.rememberMe()// Here we are overriding the default value of rememberMe() of spring security
                  //  .tokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(21))
                    //.key("Secure_Key_Has_To_Go_In_Here")
                    //.rememberMeParameter("remember-me")// This field has to match with the "name" parameter of the login.html file's remember me attribute
                //.and()
                //.logout()
                  //  .logoutUrl("/logout")// As part of logout, clear the authentication & invalidate the httpSession
                //If csrf is enabled then the default logout method is POST. If it is disabled, then default logout is get. This is not safe, hence we must use below line,  in case of csrf disabled.
                //The URL that triggers log out to occur (default is "/logout"). If CSRF protection is enabled (default), then the request must also be a POST. This means that by default POST "/logout" is required to trigger a log out. If CSRF protection is disabled, then any HTTP method is allowed.
                //It is considered best practice to use an HTTP POST on any action that changes state (i.e. log out) to protect against CSRF attacks. If you really want to use an HTTP GET, you can use logoutRequestMatcher(new AntPathRequestMatcher(logoutUrl, "GET"));
                    //.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    //.clearAuthentication(true)
                    //.invalidateHttpSession(true)
                    //.deleteCookies("JSESSION-ID", "remember-me")
                    //.logoutSuccessUrl("/login"); // on successful logout, redirect to login page

    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder); // allows for passwords to be decoded
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    /*@Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails student = User.builder()
                .username("student1")
                .password(passwordEncoder.encode("password"))
                //.roles(STUDENT.name()).build();
                .authorities(STUDENT.getGrantedAuthority())
                .build();
        UserDetails admin = User.builder()
                .username("admin1")
                .password(passwordEncoder.encode("password"))
                //.roles(ADMIN.name()).build();
                .authorities(ADMIN.getGrantedAuthority())
                .build();
        UserDetails adminTrainee = User.builder()
                .username("adminTrainee1")
                .password(passwordEncoder.encode("password"))
                //.roles(ADMINTRAINEE.name()).build();
                .authorities(ADMINTRAINEE.getGrantedAuthority())
                .build();
        return new InMemoryUserDetailsManager(student, admin, adminTrainee);
    }*/
}