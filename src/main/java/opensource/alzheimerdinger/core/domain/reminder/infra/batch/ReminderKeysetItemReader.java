package opensource.alzheimerdinger.core.domain.reminder.infra.batch;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.reminder.domain.entity.Reminder;
import opensource.alzheimerdinger.core.domain.reminder.domain.entity.ReminderStatus;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ReminderKeysetItemReader implements ItemStreamReader<Reminder>, ItemStream {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;
    private final int chunkSize;

    private String lastUserId = "0";
    private Iterator<Reminder> buffer;

    @Override
    public Reminder read() {
        log.info("Reading reminders from database...");

        if (buffer == null || !buffer.hasNext()) {
            buffer = fetch().iterator();

            if (!buffer.hasNext())
                return null;
        }

        log.info("End Reading reminders from database...");
        return buffer.next();
    }

    private List<Reminder> fetch() {
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("today", LocalDate.now(ZoneId.of("Asia/Seoul")))
                .addValue("nowTime", LocalTime.now(ZoneId.of("Asia/Seoul"))
                        .withSecond(0)
                        .withNano(0))
                .addValue("lastId", lastUserId)
                .addValue("limit", chunkSize);

        String sql =
                "select user_id, fire_time, last_sent_date " +
                        "from reminder " +
                        "where fire_time = :nowTime " +
                        "and (last_sent_date is null or last_sent_date < :today) " +
                        "and status = 'ACTIVE' " +
                        "and user_id > :lastId " +
                        "order by user_id asc " +
                        "limit :limit";

        List<Reminder> list = jdbcTemplate.query(sql, p, (rs, i) -> Reminder.builder()
                .userId(rs.getString("user_id"))
                .user(entityManager.getReference(User.class, rs.getString("user_id")))
                .fireTime(rs.getTime("fire_time").toLocalTime())
                .lastSentDate(rs.getObject("last_sent_date", LocalDate.class))
                .status(ReminderStatus.ACTIVE)
                .build());

        if (!list.isEmpty()) lastUserId = list.get(list.size() - 1).getUserId();
        return list;
    }

    @Override public void open(ExecutionContext ctx) {}
    @Override public void update(ExecutionContext ctx) {}
    @Override public void close() {}
}
