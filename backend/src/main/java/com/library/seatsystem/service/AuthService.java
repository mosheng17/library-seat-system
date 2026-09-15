package com.library.seatsystem.service;

import com.library.seatsystem.dto.LoginRequest;
import com.library.seatsystem.dto.LoginResponse;
import com.library.seatsystem.dto.RegisterRequest;
import com.library.seatsystem.entity.User;
import com.library.seatsystem.exception.BusinessException;
import com.library.seatsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService extends BaseService<User, Long> {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }

/** 登录：校验账号密码。 */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getRole()
        );
    }

/** 注册：默认学生角色。 */
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRealName(request.getRealName());
        user.setRole(com.library.seatsystem.common.BizConstants.ROLE_STUDENT);

        User savedUser = userRepository.save(user);

        return new LoginResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRealName(),
                savedUser.getRole()
        );
    }
}
