package freehold.production.goods;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoodsSeeder implements CommandLineRunner {

    private static final int TOTAL_ROWS = 10_000_000;
    private static final int BATCH_SIZE = 10_000;

    private static final GoodsSubcategory[] SUBCATEGORIES = GoodsSubcategory.values();
    private static final String[] SERBIAN_ORIGINS = {
            "Zlatibor", "Šumadija", "Fruška Gora", "Kopaonik", "Tara",
            "Vojvodina", "Negotin", "Toplica", "Srem", "Mačva",
            "Užice", "Čačak", "Valjevo", "Arilje", "Ivanjica"
    };

    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            long count = countRows(conn);
            if (count > 0) {
                log.info("Goods table already has {} rows, skipping seed", count);
                return;
            }

            log.info("Seeding {} rows...", TOTAL_ROWS);
            long start = System.currentTimeMillis();

            conn.setAutoCommit(false);
            String sql = "INSERT INTO goods (id, name, description, category, subcategory, owner_id, price, quantity, weight, origin, harvest_date) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            Faker faker = new Faker();
            ThreadLocalRandom random = ThreadLocalRandom.current();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < TOTAL_ROWS; i++) {
                    GoodsSubcategory sub = SUBCATEGORIES[random.nextInt(SUBCATEGORIES.length)];
                    GoodsCategory cat = sub.getCategory();

                    ps.setObject(1, UUID.randomUUID());
                    ps.setString(2, generateName(faker, sub));
                    ps.setString(3, faker.lorem().sentence());
                    ps.setString(4, cat.name());
                    ps.setString(5, sub.name());
                    ps.setObject(6, UUID.randomUUID());
                    ps.setBigDecimal(7, randomPrice(random));
                    ps.setInt(8, random.nextInt(1, 500));
                    ps.setBigDecimal(9, randomWeight(random));
                    ps.setString(10, SERBIAN_ORIGINS[random.nextInt(SERBIAN_ORIGINS.length)]);
                    ps.setDate(11, Date.valueOf(randomHarvestDate(random)));

                    ps.addBatch();

                    if ((i + 1) % BATCH_SIZE == 0) {
                        ps.executeBatch();
                        conn.commit();
                        if ((i + 1) % 1_000_000 == 0) {
                            log.info("Inserted {} / {} rows", i + 1, TOTAL_ROWS);
                        }
                    }
                }
                ps.executeBatch();
                conn.commit();
            }

            long elapsed = (System.currentTimeMillis() - start) / 1000;
            log.info("Seeding complete: {} rows in {}s", TOTAL_ROWS, elapsed);
        }
    }

    private long countRows(Connection conn) throws Exception {
        try (var rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM goods")) {
            rs.next();
            return rs.getLong(1);
        } catch (Exception e) {
            return 0;
        }
    }

    private String generateName(Faker faker, GoodsSubcategory sub) {
        String base = sub.name().replace("_", " ").toLowerCase();
        return base.substring(0, 1).toUpperCase() + base.substring(1) + " " + faker.address().cityName();
    }

    private BigDecimal randomPrice(ThreadLocalRandom random) {
        return BigDecimal.valueOf(random.nextDouble(50, 5000)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal randomWeight(ThreadLocalRandom random) {
        return BigDecimal.valueOf(random.nextDouble(0.1, 50.0)).setScale(2, RoundingMode.HALF_UP);
    }

    private LocalDate randomHarvestDate(ThreadLocalRandom random) {
        long startDay = LocalDate.of(2023, 1, 1).toEpochDay();
        long endDay = LocalDate.of(2026, 10, 28).toEpochDay();
        return LocalDate.ofEpochDay(random.nextLong(startDay, endDay));
    }
}
