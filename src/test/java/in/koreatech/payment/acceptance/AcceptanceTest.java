package in.koreatech.payment.acceptance;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import in.koreatech.payment.acceptance.support.DBInitializer;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(value = PER_CLASS)
public abstract class AcceptanceTest {

    private static final String ROOT_NAME = "test";
    private static final String ROOT_PASSWORD = "1234";

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private DBInitializer dbInitializer;

    @Container
    static final MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0.29")
        .withDatabaseName("test")
        .withUsername(ROOT_NAME)
        .withPassword(ROOT_PASSWORD)
        .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci")
        .withReuse(true);

    @Container
    static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.0.9"))
        .withExposedPorts(6379)
        .withReuse(true);

    @DynamicPropertySource
    private static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", () -> ROOT_NAME);
        registry.add("spring.datasource.password", () -> ROOT_PASSWORD);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    static {
        mySqlContainer.start();
        redisContainer.start();
    }

    @BeforeEach
    void clear() {
        dbInitializer.initIncrement();
        dbInitializer.clearRedis();
    }
}
