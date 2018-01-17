package io.pivotal.pal.tracker;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(MysqlDataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO time_entries(project_id, user_id, date, hours) " +
                            "values (?, ?, ?, ?)", RETURN_GENERATED_KEYS);

            int indx = 1;
            preparedStatement.setObject(indx++, timeEntry.getProjectId(), Types.BIGINT);
            preparedStatement.setObject(indx++, timeEntry.getUserId(), Types.BIGINT);
            preparedStatement.setObject(indx++, timeEntry.getDate(), Types.DATE);
            preparedStatement.setObject(indx++, timeEntry.getHours(), Types.INTEGER);

            return preparedStatement;

        },holder);

        return find(holder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long id) {

        List<TimeEntry> timeEntries = jdbcTemplate.query("SELECT * FROM time_entries WHERE id =  " + id,
                (rs, rowNum) -> {
                    TimeEntry timeEntry = new TimeEntry();
                    timeEntry.setId(rs.getLong("id"));
                    timeEntry.setProjectId(rs.getLong("project_id"));
                    timeEntry.setUserId(rs.getLong("user_id"));
                    timeEntry.setDate(rs.getDate("date").toLocalDate());
                    timeEntry.setHours(rs.getInt("hours"));
                    return timeEntry;
                }
            );

        return timeEntries.isEmpty() ? null : timeEntries.get(0);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("SELECT * FROM time_entries",
                (rs, rowNum) -> {
                    TimeEntry timeEntry = new TimeEntry();
                    timeEntry.setId(rs.getLong("id"));
                    timeEntry.setProjectId(rs.getLong("project_id"));
                    timeEntry.setUserId(rs.getLong("user_id"));
                    timeEntry.setDate(rs.getDate("date").toLocalDate());
                    timeEntry.setHours(rs.getInt("hours"));
                    return timeEntry;
                }
        );
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE time_entries " +
                            "SET project_id = ?, " +
                            " user_id = ?, " +
                            " date = ?, " +
                            " hours = ? " +
                            "WHERE id = ?");

            int indx = 1;
            preparedStatement.setObject(indx++, timeEntry.getProjectId(), Types.BIGINT);
            preparedStatement.setObject(indx++, timeEntry.getUserId(), Types.BIGINT);
            preparedStatement.setObject(indx++, timeEntry.getDate(), Types.DATE);
            preparedStatement.setObject(indx++, timeEntry.getHours(), Types.INTEGER);
            preparedStatement.setObject(indx++, id, Types.BIGINT);

            return preparedStatement;

        });

        return find(id);
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "delete from time_entries " +
                            "WHERE id = ?");

            preparedStatement.setObject(1, id, Types.BIGINT);

            return preparedStatement;

        });
    }
}
