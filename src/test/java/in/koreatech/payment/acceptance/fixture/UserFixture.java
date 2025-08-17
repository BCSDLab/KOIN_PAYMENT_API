package in.koreatech.payment.acceptance.fixture;

import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.user.model.User;
import in.koreatech.koin.domain.user.repository.UserRepository;
import in.koreatech.payment.common.auth.JwtProvider;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class UserFixture {

    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    public UserFixture(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    public User 코인_유저() {
        return userRepository.save(User.builder()
            .id(1)
            .build()
        );
    }

    public String getToken(User user) {
        return jwtProvider.createToken(user);
    }
}
