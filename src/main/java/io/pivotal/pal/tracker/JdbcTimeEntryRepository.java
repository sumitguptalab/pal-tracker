package io.pivotal.pal.tracker;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    private String updateQuery = "UPDATE time_entries  " +
            "              SET project_id = ?,  " +
            "                  user_id = ?,  " +
            "                  date = ?,  " +
            "                  hours = ?  " +
            "              WHERE id = ?";

    public JdbcTimeEntryRepository(DataSource dataSource) {
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

        }, holder);

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
        // First Way
//        jdbcTemplate.update(connection -> {
//            PreparedStatement preparedStatement = connection.prepareStatement(
//                    "UPDATE time_entries " +
//                            "SET project_id = ?, " +
//                            " user_id = ?, " +
//                            " date = ?, " +
//                            " hours = ? " +
//                            "WHERE id = ?");
//
//            int indx = 1;
//            preparedStatement.setObject(indx++, timeEntry.getProjectId(), Types.BIGINT);
//            preparedStatement.setObject(indx++, timeEntry.getUserId(), Types.BIGINT);
//            preparedStatement.setObject(indx++, timeEntry.getDate(), Types.DATE);
//            preparedStatement.setObject(indx++, timeEntry.getHours(), Types.INTEGER);
//            preparedStatement.setObject(indx++, id, Types.BIGINT);
//
//            return preparedStatement;
//
//        });

        // Second Way
//        jdbcTemplate.update(new UpdatePreparedStatmentCreator(id,timeEntry));

        //Third Ways
        jdbcTemplate.execute(new UpdatePreparedStatmentCreator(id,timeEntry), new PreparedStatementCallback() {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                return ps.executeUpdate();
            }
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

    private class UpdatePreparedStatmentCreator implements PreparedStatementCreator {
        long id;
        TimeEntry timeEntry;
        public UpdatePreparedStatmentCreator(long id, TimeEntry timeEntry) {
            this.id = id;
            this.timeEntry = timeEntry;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement preparedStatement = con.prepareStatement(updateQuery);
            int indx = 1;
            preparedStatement.setObject(indx++, timeEntry.getProjectId(), Types.BIGINT);
            preparedStatement.setObject(indx++, timeEntry.getUserId(), Types.BIGINT);
            preparedStatement.setObject(indx++, timeEntry.getDate(), Types.DATE);
            preparedStatement.setObject(indx++, timeEntry.getHours(), Types.INTEGER);
            preparedStatement.setObject(indx++, id, Types.BIGINT);

            return preparedStatement;
        }
    }

}
