package in.koreatech.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.koin.domain.user.model.User;
import in.koreatech.koin.domain.user.repository.UserRepository;
import in.koreatech.payment.common.auth.JwtProvider;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "koinTransactionManager", readOnly = true)
public class UserAuthenticationService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public User authenticateUser(String accessToken) {
        Integer userId = jwtProvider.getUserId(accessToken);
        return userRepository.getById(userId);
    }
}
