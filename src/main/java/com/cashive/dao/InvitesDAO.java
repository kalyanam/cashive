package com.cashive.dao;

import com.cashive.dto.Group;
import com.cashive.dto.Invite;
import com.cashive.exceptions.CashiveException;
import com.cashive.exceptions.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mkalyan on 8/27/16.
 */
public class InvitesDAO {
    private static final Logger logger = LoggerFactory.getLogger(InvitesDAO.class);
    private JdbcTemplate jdbcTemplate;

    private AccountsDAO accountsDAO;
    private GroupsDAO groupsDAO;

    private static final String INSERT_NEW = "INSERT INTO INVITES (GROUP_ID, INVITER_ID, INVITED_EMAIL, INVITE_DATE) " +
            "VALUES ('%d', '%d', '%s', now())";
    private static final String FIND_ALL_FOR_USER = "SELECT GROUP_ID, INVITER_ID, INVITE_DATE FROM INVITES " +
            "WHERE INVITED_EMAIL = '%s'";

    public InvitesDAO(JdbcTemplate jdbcTemplate, AccountsDAO accountsDAO, GroupsDAO groupsDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountsDAO = accountsDAO;
        this.groupsDAO = groupsDAO;
    }

    public void createNewInvite(Integer groupId, String inviterEmail, String invitedEmail) {
        //does the group exist?
        Group group = groupsDAO.findGroupById(groupId);
        if(group == null) {
            logger.info("No group exists with id: {}", groupId);
            throw new CashiveException("No group exists with this id", ErrorCode.NO_GROUP_WITH_ID);
        }

        //check if group is already active, if so can't send an invite
        Integer inviterId = accountsDAO.getAccountId(inviterEmail);
        String sql = String.format(INSERT_NEW, groupId, inviterId, invitedEmail);
        logger.info("Executing sql: {}", sql);
        jdbcTemplate.execute(sql);
    }

    public void createNewInvites(Integer groupId, String inviterEmail, List<String> invitedEmails) {
        //does the group exist?
        Group group = groupsDAO.findGroupById(groupId);
        if(group == null) {
            logger.info("No group exists with id: {}", groupId);
            throw new CashiveException("No group exists with this id", ErrorCode.NO_GROUP_WITH_ID);
        }

        //check if group is already active, if so can't send an invite
        Integer inviterId = accountsDAO.getAccountId(inviterEmail);
        StringBuffer sb = new StringBuffer();
        invitedEmails.forEach(email -> {
            sb.append(String.format(INSERT_NEW, groupId, inviterId, email));
            sb.append(";");
        });
        String sql = sb.toString();
        logger.info("Executing sql: {}", sql);
        jdbcTemplate.execute(sql);
    }

    public List<Invite> findAllInvitesForUser(String emailId) {
        String sql = String.format(FIND_ALL_FOR_USER, emailId);
        logger.info("Executing sql: {}", sql);

        return jdbcTemplate.query(sql, new RowMapper<Invite>() {
            @Override
            public Invite mapRow(ResultSet resultSet, int i) throws SQLException {
                Invite invite = new Invite();
                invite.setGroupId(resultSet.getInt("group_id"));
                invite.setInviterId(resultSet.getInt("inviter_id"));
                invite.setInvitedEmail(emailId);
                invite.setInviteDate(new Date(resultSet.getDate("invite_date").getTime()));

                return invite;
            }
        });
    }
}
