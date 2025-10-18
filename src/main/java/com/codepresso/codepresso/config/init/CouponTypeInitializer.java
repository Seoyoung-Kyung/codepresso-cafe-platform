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
@Order(Ordered.HIGHEST_PRECEDENCE + 6)
@RequiredArgsConstructor
public class CouponTypeInitializer implements ApplicationRunner {

    private final DataSource dataSource;

    @Value("classpath:db/seed/coupon_type_seed_insert.sql")
    private Resource couponTypeSeed;

    @Override
    @Transactional
    public void run(ApplicationArguments args){
        log.info("[Init] CouponTypeInitializer start");

        int existing = countCouponTypes();
        if (existing > 0) {
            log.info("[Init] coupon_type table already seeded ({} rows), skip", existing);
            return;
        }

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(couponTypeSeed);
        populator.setSeparator(";");
        populator.setContinueOnError(true);
        populator.execute(dataSource);

        log.info("[Init] coupon_type rows now: {}", countCouponTypes());
        log.info("[Init] CouponTypeInitializer done");
    }

    private int countCouponTypes() {
        try(Connection c = dataSource.getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM coupon_type")) {
            return rs.next() ?rs.getInt(1):0;
        }catch (Exception e) {
            log.warn("[Init] coupon_type count failed", e);
            return 0;
        }
    }

}
