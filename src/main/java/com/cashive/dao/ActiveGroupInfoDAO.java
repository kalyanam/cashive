package com.cashive.dao;

import com.cashive.dto.ActiveGroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by mkalyan on 8/27/16.
 */
public class ActiveGroupInfoDAO {
    private static final Logger logger = LoggerFactory.getLogger(ActiveGroupInfoDAO.class);
    private JdbcTemplate jdbcTemplate;
    private GroupsDAO groupsDAO;
    private AccountsDAO accountsDAO;

    private static final String ACTIVATE_GROUP = "INSERT INTO ACTIVE_GROUP_INFO (GROUP_ID, CURRENT_TERM, PARTICIPANT_SIZE, ACTIVATED_DATE) " +
            "VALUES ('%d', '1', '%d', now())";
    private static final String FIND_ACTIVE_GROUPS_FOR_USER = "SELECT GROUP_ID, CURRENT_TERM, PARTICIPANT_SIZE, ACTIVATED_DATE " +
            "FROM ACTIVE_GROUP_INFO WHERE GROUP_ID IN (SELECT GROUP_ID FROM GROUPS WHERE STARTED_BY IN (SELECT ACCOUNT_ID FROM ACCOUNTS WHERE EMAIL = '%s'))";

    public ActiveGroupInfoDAO(JdbcTemplate jdbcTemplate, GroupsDAO groupsDAO, AccountsDAO accountsDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.groupsDAO = groupsDAO;
        this.accountsDAO = accountsDAO;
    }

    public void activateGroup(Integer groupId, Integer participantSize) {
        String sql = String.format(ACTIVATE_GROUP, groupId, participantSize);
        logger.info("Executing sql: {}", sql);

        jdbcTemplate.execute(sql);
    }

    public List<ActiveGroupInfo> findActiveGroupsForUser(String email) {
        String sql = String.format(FIND_ACTIVE_GROUPS_FOR_USER, email);
        logger.info("Executing sql: {}", sql);

        return jdbcTemplate.query(sql, new RowMapper<ActiveGroupInfo>() {
            @Override
            public ActiveGroupInfo mapRow(ResultSet resultSet, int i) throws SQLException {
                ActiveGroupInfo info = new ActiveGroupInfo();
                info.setGroupId(resultSet.getInt("group_id"));
                info.setCurrentTerm(resultSet.getInt("current_term"));
                info.setParticipantSize(resultSet.getInt("participant_size"));
                info.setActivatedDate(new Date(resultSet.getDate("activated_date").getTime()));

                return info;
            }
        });
    }

}
