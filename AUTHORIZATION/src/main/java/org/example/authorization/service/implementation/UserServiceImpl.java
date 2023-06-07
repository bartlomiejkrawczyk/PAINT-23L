package org.example.authorization.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.authorization.entity.UserEntity;
import org.example.authorization.exception.IncorrectLogin;
import org.example.authorization.exception.PasswordsDoNotMatch;
import org.example.authorization.exception.WeakPassword;
import org.example.authorization.model.LoginRequest;
import org.example.authorization.model.RegisterRequest;
import org.example.authorization.model.User;
import org.example.authorization.repository.UserRepository;
import org.example.authorization.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<User> login(LoginRequest request) {
        return userRepository.findByLogin(request.getLogin())
                .mapNotNull(it -> tryLogin(it, request));
    }

    @Override
    public Mono<Boolean> register(RegisterRequest request) {
        validatePassword(request.getPassword(), request.getConfirmPassword());

        return userRepository.save(
                UserEntity
                        .builder()
                        .login(request.getLogin())
                        .passwordHash(passwordEncoder.encode(request.getPassword()))
                        .build()
        ).map(Objects::nonNull);
    }

    private User tryLogin(UserEntity userEntity, LoginRequest request) {
        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPasswordHash()))
            throw new IncorrectLogin();
        return User.builder()
                .id(userEntity.getUserId())
                .login(userEntity.getLogin())
                .build();
    }

    private void validatePassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword))
            throw new PasswordsDoNotMatch();
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c))
                hasUpperCase = true;
            else if (Character.isLowerCase(c))
                hasLowerCase = true;
            else if (Character.isDigit(c))
                hasDigit = true;
            if (hasUpperCase && hasLowerCase && hasDigit)
                break;
        }
        if (!hasUpperCase || !hasLowerCase || !hasDigit)
            throw new WeakPassword();
    }
}
