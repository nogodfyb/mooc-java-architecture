package com.example.tccdemo.service;

import com.example.tccdemo.db131.dao.AccountAMapper;
import com.example.tccdemo.db131.model.AccountA;
import com.example.tccdemo.db132.dao.AccountBMapper;
import com.example.tccdemo.db132.model.AccountB;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class AccountService {
    @Resource
    private AccountAMapper accountAMapper;
    @Resource
    private AccountBMapper accountBMapper;

    /**
     * 事务补偿机制
     */
    @Transactional(transactionManager = "tm131", rollbackFor = Exception.class)
    public void transferAccount() {
        // 账户A 减200
        AccountA accountA = accountAMapper.selectByPrimaryKey(1);
        accountA.setBalance(accountA.getBalance().subtract(new BigDecimal(200)));
        accountAMapper.updateByPrimaryKey(accountA);

        // 账户B 加200
        AccountB accountB = accountBMapper.selectByPrimaryKey(2);
        accountB.setBalance(accountB.getBalance().add(new BigDecimal(200)));
        accountBMapper.updateByPrimaryKey(accountB);

        // 捕获异常 手动写代码回滚账户B的操作
        try{
            int i = 1/0;
        }catch (Exception e){

            try{
                AccountB accountb = accountBMapper.selectByPrimaryKey(2);
                accountb.setBalance(accountb.getBalance().subtract(new BigDecimal(200)));
                accountBMapper.updateByPrimaryKey(accountb);
            }catch (Exception e1){

            }


            throw e;
        }


    }


}
