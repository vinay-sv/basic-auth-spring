package com.example.demo.auth;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.demo.security.ApplicationUserRole.*;

@Repository("fake")
public class FakeApplicationUserDaoImpl implements ApplicationUserDao {

    private final PasswordEncoder passwordEncoder;


    public FakeApplicationUserDaoImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return getApplicationUsers()
                .stream()
                .filter(applicationUser -> applicationUser.getUsername().equals(username))
                .findFirst();
    }

    private List<ApplicationUser> getApplicationUsers() {
        List<ApplicationUser> applicationUsers = Lists.newArrayList(
                new ApplicationUser("student1",
                        passwordEncoder.encode("password"),
                        STUDENT.getGrantedAuthority(),
                        true,
                        true,
                        true,
                        true ),

                new ApplicationUser("admin1",
                        passwordEncoder.encode("password"),
                        ADMIN.getGrantedAuthority(),
                        true,
                        true,
                        true,
                        true ),
                new ApplicationUser("admintrainee1",
                        passwordEncoder.encode("password"),
                        ADMINTRAINEE.getGrantedAuthority(),
                        true,
                        true,
                        true,
                        true )
                );
        return applicationUsers;
    }
}
