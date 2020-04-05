package com.example.demo.mapper;


import com.example.demo.pojo.Account;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AccountMapper extends Mapper<Account> {

    List<Account> queryAccountList() throws Exception;

}
