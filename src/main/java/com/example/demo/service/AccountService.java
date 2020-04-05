package com.example.demo.service;

import com.example.demo.pojo.Account;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

public interface AccountService {

    List<Account> queryAccountList() throws Exception;

}
