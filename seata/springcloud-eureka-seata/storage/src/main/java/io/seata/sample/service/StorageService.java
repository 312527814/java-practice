package io.seata.sample.service;

import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

/**
 * @author jimin.jm@alibaba-inc.com
 * @date 2019/06/14
 */
@Service
public class StorageService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Transactional
//    @GlobalTransactional
    public void deduct(String commodityCode, int count) {

        jdbcTemplate.update("update storage_tbl set count = count - ? where commodity_code = ?",
            new Object[] {count, commodityCode});


        UUID uuid = UUID.randomUUID();
//        jdbcTemplate.update("update storage_tbl set count = count - ? where commodity_code in ('C100000','C1000001')",new Object[] {count});
//        jdbcTemplate.update("INSERT INTO `at_storage`.`storage_tbl` (`commodity_code`, `count`) VALUES ('" + uuid.toString() + "', 99031)");
//
//        jdbcTemplate.update("INSERT INTO `at_storage`.`storage_tbl` (`commodity_code`, `count`) VALUES ('" + uuid.toString() + "1" + "', 99031)");


    }
}
