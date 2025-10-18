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
@Order(Ordered.HIGHEST_PRECEDENCE + 3) // BranchInitializer 다음
@RequiredArgsConstructor
public class BoardTypeInitializer implements ApplicationRunner {

    private final DataSource dataSource;

    @Value("classpath:db/seed/board_seed_insert.sql")
    private Resource boardTypeSeed;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("[Init] BoardTypeInitializer start");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(boardTypeSeed);
        populator.setSeparator(";");          // 문장 구분자
        populator.setContinueOnError(true);   // 일부 레코드 실패해도 계속
        populator.execute(dataSource);        // INSERT IGNORE라 중복은 무시됨

        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM board_type")) {
            if (rs.next()) {
                log.info("[Init] board_type rows now: {}", rs.getInt(1));
            }
        } catch (Exception e) {
            log.warn("[Init] count failed", e);
        }

        log.info("[Init] BoardTypeInitializer done");
    }
}
