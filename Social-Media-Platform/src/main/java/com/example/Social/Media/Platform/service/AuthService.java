package com.example.Social.Media.Platform.service;

import com.example.Social.Media.Platform.dto.*;
import com.example.Social.Media.Platform.model.Role;
import com.example.Social.Media.Platform.model.User;
import com.example.Social.Media.Platform.model.UserInfo;
import com.example.Social.Media.Platform.model.VerificationStatus;
import com.example.Social.Media.Platform.repository.LikeRepository;
import com.example.Social.Media.Platform.repository.PostRepository;
import com.example.Social.Media.Platform.repository.UserRepository;
import com.example.Social.Media.Platform.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public String register(RegisterRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .verificationStatus(VerificationStatus.PENDING)
                .verificationToken(verificationToken)
                .tokenExpiry(LocalDateTime.now().plusHours(24))
                .role(Role.ROLE_USER)
                .build();

        UserInfo userInfo = UserInfo.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .user(user)
                .build();

        user.setUserInfo(userInfo);
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        return "Registration successful. Please check your email for verification.";
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getVerificationStatus() == VerificationStatus.PENDING) {
            throw new RuntimeException("Please verify your email before logging in.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(request.getEmail()));

        return AuthResponseDTO.builder()
                .token(token)
                .user(mapToUserResponseDTO(user))
                .build();
    }

    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired");
        }

        user.setVerificationStatus(VerificationStatus.VERIFIED);
        user.setVerificationToken(null);
        userRepository.save(user);

        return "Email verified successfully. You can now login.";
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getUserInfo().getFirstName())
                .lastName(user.getUserInfo().getLastName())
                .role(user.getRole().name())
                .verificationStatus(user.getVerificationStatus().name())
                .postCount(postRepository.countByUserId(user.getId()))
                .likeCount(likeRepository.countLikesByUserId(user.getId()))
                .build();
    }
}
