package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static com.example.demo.security.ApplicationUserPermission.COURSE_WRITE;
import static com.example.demo.security.ApplicationUserPermission.STUDENT_WRITE;
import static com.example.demo.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() //TODO: Understand what this is for
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
                .authenticated()
                .and()
                .httpBasic();
    }

    @Override
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
    }
}