package com.project.blog.service;

import com.project.blog.dao.IUserRepository;
import com.project.blog.dto.LoginRequest;
import com.project.blog.dto.RegisterRequest;
import com.project.blog.model.AuthenticationResponse;
import com.project.blog.model.User;
import com.project.blog.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    public void signUp(RegisterRequest registerRequest) {
        User user = new User();
        user.setUserName(registerRequest.getUsername());
        user.setPassword(getEncodedPwd(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        userRepository.save(user);
    }

    private String getEncodedPwd(String pwd) {
        return passwordEncoder.encode(pwd);
    }

    /**
     * 1. AuthenticationManager.authenticate(Authentication)
     *  - AuthenticationManager is a interface but we can use AuthenticationManagerBuilder
     *    to create its impl, and provide UserDetailsService (with loadUserByUsername)
     * 2. save the Authentication SecurityContext
     */
    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword());
        Authentication authentication1 = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication1);
        String token = jwtProvider.generateToken(authentication1);
        return new AuthenticationResponse(token, jwtProvider.getUsernameFromJwt(token));
    }

    public Optional<org.springframework.security.core.userdetails.UserDetails> getUsername() {
        UserDetails principal = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Optional.of(principal);
    }

}
