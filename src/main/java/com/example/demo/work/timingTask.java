package com.example.demo.work;

import com.alibaba.fastjson.JSON;
import com.example.demo.domain.User;
import com.example.demo.service.impl.UserServiceImpl;
import com.example.demo.util.EncryptionKey;
import com.example.demo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 定时任务
 *
 * @author <247702560@qq.com>
 * @since 2021/6/14 13:01
 */
@Component
@Slf4j
public class timingTask {
    /*redis 缓存*/
    @Resource
    private RedisUtil cache;

    @Resource
    private UserServiceImpl userService;

    /**
     * 描述: 每小时执行一次，检车用户余额是否充足。
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/14 12:16
     * @param:
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void checkUserBalance() {
        Map<Object, Object> map = cache.hmget(EncryptionKey.loginIpAddress);
        for (var e : map.entrySet()) {
            String ipAddress = (String) e.getValue();
            User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);
            BigDecimal costMoney = userService.calcUserCostMoney(ipAddress);
            log.info("userInfo:{}, ipAddress:{}, costMoney:{}", JSON.toJSON(user), ipAddress, costMoney);
            //花费的金额比余额还大，强制令其下线！
            if (costMoney.compareTo(user.getBalance()) >= 0) {
                userService.logOutHandler(ipAddress);
            }
        }
    }
}
