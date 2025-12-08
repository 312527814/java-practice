package io.seata.sample.controller;

import io.seata.sample.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author jimin.jm@alibaba-inc.com
 * @date 2019/06/14
 */

@RestController
public class BusinessController {
    String[] commodityCodes = new String[]{
            "C100000", "C1000001", "C1000002", "C1000003", "C1000004", "C1000005", "C1000006", "C1000007", "C1000008", "C1000009",
            "C10000010", "C10000011", "C10000012", "C10000013", "C10000014", "C10000015", "C10000016", "C10000017", "C10000018", "C10000019",
            "C10000020", "C10000021", "C10000022", "C10000023", "C10000024", "C10000025", "C10000026", "C10000027", "C10000028", "C10000029",
            "C10000030", "C10000031", "C10000032", "C10000033", "C10000034", "C10000035", "C10000036", "C10000037", "C10000038", "C10000039",
            "C10000040", "C10000041", "C10000042", "C10000043", "C10000044", "C10000045", "C10000046", "C10000047", "C10000048", "C10000049",

    };
    String[] users = new String[]{
            "U100000", "U100001", "U100002", "U100003", "U100004", "U100005",
            "U100006", "U100007", "U100008", "U100009", "U1000010", "U1000011"
    };

    @Autowired
    private BusinessService businessService;

    /**
     * 购买下单，模拟全局事务提交
     *
     * @return
     */
    @RequestMapping(value = "/purchase/commit", produces = "application/json")
    public String purchaseCommit() {


        int max = 100000, min = 1;
        int ran2 = (int) (Math.random() * (max - min) + min);

        int ran3 = (int) (Math.random() * (max - min) + min);

        System.out.println(".....................ran=>" + ran2);
        String user = users[ran2 % users.length];
        String commodityCode = commodityCodes[ran3 % commodityCodes.length];

        try {
            businessService.purchase("U100000", "C100000", 1);


//        businessService.purchase("U100000", "C100000", 1);
        } catch (Exception exx) {
//            exx.printStackTrace();

            System.out.println("user=>" + user + " commodityCode=>" + commodityCode + "|" + exx.getMessage());
            throw new RuntimeException("user=>" + user + " commodityCode=>" + commodityCode + "|" + exx.getMessage());
        }


        return "全局事务提交:user=>" + user + " commodityCode=>" + commodityCode;
    }

    /**
     * 购买下单，模拟全局事务提交
     *
     * @return
     */
    @RequestMapping(value = "/purchase/commit2", produces = "application/json")
    public String purchaseCommit(String userId) {


        int max = 100000, min = 1;
        int ran2 = (int) (Math.random() * (max - min) + min);

        int ran3 = (int) (Math.random() * (max - min) + min);

        System.out.println(".....................ran=>" + ran2);
        String user = users[ran2 % users.length];
        String commodityCode = commodityCodes[ran3 % commodityCodes.length];

        try {
            businessService.purchase(userId, "C100000", 1);


//        businessService.purchase("U100000", "C100000", 1);
        } catch (Exception exx) {
//            exx.printStackTrace();

            System.out.println("user=>" + user + " commodityCode=>" + commodityCode + "|" + exx.getMessage());
            throw new RuntimeException("user=>" + user + " commodityCode=>" + commodityCode + "|" + exx.getMessage());
        }


        return "全局事务提交:user=>" + user + " commodityCode=>" + commodityCode;
    }

    /**
     * 购买下单，模拟全局事务回滚
     * 账户或库存不足
     *
     * @return
     */
    @RequestMapping("/purchase/rollback")
    public String purchaseRollback() {
        try {
            businessService.purchase("U100000", "C100000", 99999);
        } catch (Exception exx) {
            return exx.getMessage();
        }
        return "全局事务提交";
    }
}
