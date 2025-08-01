package opensource.alzheimerdinger.core.domain.reminder.infra.batch;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.notification.domain.service.NotificationService;
import opensource.alzheimerdinger.core.domain.reminder.domain.entity.Reminder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReminderItemWriter implements ItemWriter<Reminder> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final NotificationService notificationService;

    @Override
    public void write(Chunk<? extends Reminder> chunk) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        List<Reminder> list = (List<Reminder>) chunk.getItems();
        List<String>   userIds = list.stream().map(Reminder::getUserId).toList();

        Map<String, String> tokenMap = fetchTokens(userIds);

        list.forEach(r -> {
            String token = tokenMap.get(r.getUserId());
            if (token != null) {
                notificationService.sendNotification(token, "리마인드", "설정하신 시간이 되었습니다.", r.getUserId());
                r.updateLastSent(today);
            }
        });

        jdbcTemplate.getJdbcOperations().batchUpdate(
                "update reminder set last_sent_date = ? where user_id = ?",
                list,
                500,
                (ps, r) -> {
                    ps.setDate(1, Date.valueOf(r.getLastSent()));
                    ps.setString(2, r.getUserId());
                });
    }

    private Map<String, String> fetchTokens(List<String> userIds) {
        if (userIds.isEmpty())
            return Map.of();

        String sql = "select user.user_id, token from fcm_token where user_id in (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("ids", userIds);

        return jdbcTemplate.query(sql, params, rs -> {
            Map<String, String> map = userIds.stream()
                    .collect(Collectors.toMap(id -> id, id -> null));

            while (rs.next()) {
                map.put(
                        rs.getString("user_id"),
                        rs.getString("device_token")
                );
            }

            return map;
        });
    }
}
