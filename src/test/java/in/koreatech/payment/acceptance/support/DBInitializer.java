package in.koreatech.payment.acceptance.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@TestConfiguration
public class DBInitializer {

    private static final int OFF = 0;
    private static final int ON = 1;

    private List<String> tableNames = new ArrayList<>();

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RedisTemplate<?, ?> redisTemplate;

    private void findDatabaseTableNames() {
        tableNames = entityManager.createNativeQuery("SHOW TABLES").getResultList();
    }

    private void truncateAllTable() {
        setForeignKeyChecks(OFF);
        for (String tableName: tableNames) {
            entityManager.createNativeQuery(String.format("TRUNCATE TABLE `%s`", tableName)).executeUpdate();
        }
        setForeignKeyChecks(ON);
    }

    private void setForeignKeyChecks(int mode) {
        entityManager.createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS = %d", mode)).executeUpdate();
    }

    @Transactional
    public void initIncrement() {
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'test' AND AUTO_INCREMENT >= 1";
        List<String> dirtyTables = entityManager.createNativeQuery(sql).getResultList();
        for (String dirtyTable: dirtyTables) {
            entityManager.createNativeQuery(String.format("ALTER TABLE %s AUTO_INCREMENT = 1", dirtyTable)).executeUpdate();
        }
    }

    public void clearRedis() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
}
