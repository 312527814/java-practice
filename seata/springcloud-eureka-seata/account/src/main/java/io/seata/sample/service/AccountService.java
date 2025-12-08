package io.seata.sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author jimin.jm@alibaba-inc.com
 * @date 2019/06/14
 */
@Service
public class AccountService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Transactional
    public void reduce(String userId, int money) {
        jdbcTemplate.update("update account_tbl set money = money - ? where user_id = ?", new Object[] {money, userId});

//        UUID uuid = UUID.randomUUID();
//        jdbcTemplate.update("insert into account_tbl(user_id,money) values ('" + uuid.toString() + "',1)");
    }
}
