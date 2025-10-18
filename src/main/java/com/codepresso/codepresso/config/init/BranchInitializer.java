package com.codepresso.codepresso.config.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // MemberInitializer 다음
@RequiredArgsConstructor
public class BranchInitializer implements ApplicationRunner {

    private final DataSource dataSource;

    // 위에서 복사한 경로대로!
    @Value("classpath:db/seed/branch_seed_insert_idempotent.sql")
    private Resource branchSeed;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("[Init] BranchInitializer start");

        int existing = countBranches();
        if (existing > 0) {
            log.info("[Init] branch table already seeded ({} rows), skip seeding", existing);
            return;
        }

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(branchSeed);
        populator.setSeparator(";");          // 문장 구분자
        populator.setContinueOnError(true);   // 일부 레코드 실패해도 계속
        populator.execute(dataSource);        // INSERT IGNORE라 중복은 무시됨

        log.info("[Init] branch rows now: {}", countBranches());

        log.info("[Init] BranchInitializer done");
    }

    private int countBranches() {
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM branch")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            log.warn("[Init] count failed", e);
            return 0;
        }
    }
}
