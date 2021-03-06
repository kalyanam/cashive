package com.cashive.dao;

import com.cashive.exceptions.CashiveException;
import com.cashive.exceptions.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

/**
 * Created by mkalyan on 8/26/16.
 */
public class AccountsDAO {
    private static final Logger logger = LoggerFactory.getLogger(AccountsDAO.class);

    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_NEW = "INSERT INTO ACCOUNTS (EMAIL, SALT, PASSHASH, IS_VERIFIED, IS_ACTIVE, JOINED_DATE) VALUES ('%s', '%s', '%s', 'N', 'Y', now())";
    private static final String VALIDATE_ACCOUNT = "SELECT EMAIL, SALT, PASSHASH FROM ACCOUNTS WHERE EMAIL = '%s'";
    private static final String GET_ACCOUNT_ID = "SELECT ACCOUNT_ID FROM ACCOUNTS WHERE EMAIL='%s'";

    public AccountsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Package protected method. Only other DAOs should be able to access this one
    Integer getAccountId(String email) {
        String sql = String.format(GET_ACCOUNT_ID, email);
        logger.info("Executing sql: {}", sql);
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        if(results.size() > 1 ) {
            logger.error("More than one account exists for: {}", email);
            throw new CashiveException("More than one account exists for this email id", ErrorCode.ACCOUNT_WITH_EMAIL_EXISTS);
        } else if(results.size() == 0) {
            logger.error("No account exists for : {}", email);
            throw new CashiveException("No account exists for this email id", ErrorCode.NO_ACCOUNT_EXISTS_WITH_EMAIL);
        } else {
            return Integer.parseInt(results.get(0).get("account_id").toString());
        }
    }

    public void createNewAccount(String email, String password) {
        String sql = String.format(VALIDATE_ACCOUNT, email);
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        if(results.size() > 0 ) {
            logger.error("Account exists for: {}", email);
            throw new CashiveException("Account already exists for this email id", ErrorCode.ACCOUNT_WITH_EMAIL_EXISTS);
        }

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        String stringedSalt = toHexString(salt);
        String saltedPassword = password + stringedSalt;
        byte[] hashedPassword = getHashed(saltedPassword.getBytes());
        String stringedHashedPassword = toHexString(hashedPassword);

        sql = String.format(INSERT_NEW, email, stringedSalt, stringedHashedPassword);

        logger.info("Creating a new user: {}", sql);
        jdbcTemplate.execute(sql);
    }

    public boolean validateAccount(String email, String password) {
        String sql = String.format(VALIDATE_ACCOUNT, email);
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        if(results.size() == 0 ) {
            logger.error("No user exists for: {}", email);
            return false;
        }

        if(results.size() > 1) {
            logger.error("More than one account exists for : {}", email);
            return false;
        }

        Map<String, Object> entry = results.get(0);
        logger.info("Obtained row: {}", entry);

        String stringedSalt = entry.get("salt").toString();
        String saltedPassword = password + stringedSalt;
        byte[] hashedPassword = getHashed(saltedPassword.getBytes());
        String stringedHashedPassword = toHexString(hashedPassword);

        if(stringedHashedPassword.equals(entry.get("passhash").toString())) {
            logger.info("User is authenticated: {}", email);
            return true;
        }

        return false;
    }

    //http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    private static String toHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    private static byte[] toByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }

    private byte[] getHashed(byte[] bytes) {
        byte[] res = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(bytes);
            res = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return res;
    }
}
