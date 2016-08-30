package com.cashive.dao;

import com.cashive.dto.Group;
import com.cashive.exceptions.CashiveException;
import com.cashive.exceptions.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by mkalyan on 8/27/16.
 */
public class GroupsDAO {
    private static final Logger logger = LoggerFactory.getLogger(GroupsDAO.class);
    private JdbcTemplate jdbcTemplate;
    private AccountsDAO accountsDAO;

    private static final String INSERT_NEW = "INSERT INTO GROUPS (NAME, STARTED_BY, START_DATE, TERM_LENGTH, TERM_FREQUENCY, TERM_AMOUNT, MAX_PARTICIPANTS, CREATED_DATE) " +
            "VALUES ('%s', '%d', '%s', '%d', '%s', '%d', '%d', now())";
    private static final String FIND_GROUP_BY_NAME = "SELECT COUNT(*) FROM GROUPS WHERE NAME = '%s' AND STARTED_BY = '%d'";
    private static final String FIND_GROUP_BY_ID = "SELECT NAME, STARTED_BY, START_DATE, TERM_LENGTH, TERM_FREQUENCY, TERM_AMOUNT, MAX_PARTICIPANTS, CREATED_DATE " +
            "FROM GROUPS WHERE GROUP_ID = '%d'";

    public GroupsDAO(JdbcTemplate jdbcTemplate, AccountsDAO accountsDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountsDAO = accountsDAO;
    }

    public void createNewGroup(String groupName, String email, Long startDateMillis, Integer termLength, String termFrequency, Integer termAmount, Integer maxParticipants) {
        Integer startedBy = accountsDAO.getAccountId(email);

        String sql = String.format(FIND_GROUP_BY_NAME, groupName, startedBy);
        logger.info("Executing sql: {}", sql);
        Integer rowCount = jdbcTemplate.queryForObject(sql, Integer.class);
        if(rowCount > 0) {
            logger.error("A group with this name already exists for this account: {}, {}", groupName, email);
            throw new CashiveException("A group with this name already exists for this account", ErrorCode.GROUP_WITH_NAME_ALREADY_EXISTS);
        }

        Date startDate = new Date(startDateMillis);

        sql = String.format(INSERT_NEW, groupName, startedBy, startDate, termLength, termFrequency, termAmount, maxParticipants);
        logger.info("Executing sql: {}", sql);
        jdbcTemplate.execute(sql);
    }

    public Group findGroupById(Integer groupId) {
        String sql = String.format(FIND_GROUP_BY_ID, groupId);
        logger.info("Executing sql: {}", sql);
        List<Group> groups = jdbcTemplate.query(sql, new RowMapper<Group>() {
            @Override
            public Group mapRow(ResultSet resultSet, int i) throws SQLException {
                Group group = new Group();
                group.setGroupId(groupId);
                group.setName(resultSet.getString("name"));
                group.setStartedBy(resultSet.getInt("started_by"));
                group.setStartDate(new java.util.Date(resultSet.getDate("start_date").getTime()));
                group.setTermLength(resultSet.getInt("term_length"));
                group.setTermFrequency(resultSet.getString("term_frequency"));
                group.setTermAmount(resultSet.getInt("term_amount"));
                group.setMaxParticipants(resultSet.getInt("max_participants"));
                group.setCreatedDate(new java.util.Date(resultSet.getDate("created_date").getTime()));

                return group;
            }
        });

        if(groups.size() == 0) {
            return null;
        } else {
            return groups.get(0);
        }
    }

}
