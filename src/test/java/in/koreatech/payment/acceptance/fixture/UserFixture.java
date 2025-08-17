package in.koreatech.payment.acceptance.fixture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.user.model.User;
import in.koreatech.koin.domain.user.repository.UserRepository;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class UserFixture {

    private final UserRepository userRepository;

    @Autowired
    public UserFixture(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User 코인_유저() {
        return userRepository.save(User.builder()
            .id(1)
            .build()
        );
    }
}
