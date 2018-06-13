package org.csu.mypetstorespring.controller;

import org.csu.mypetstorespring.domain.Account;
import org.csu.mypetstorespring.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@SessionAttributes({"account"})
public class AccountController {

    @Autowired
    private AccountService accountService;

    // 日志
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    // 跳往登录界面
    @GetMapping("/account/viewLoginForm")
    public String viewLoginForm(){
        logger.info("进入登录界面"); // 输出DEBUG级别的日志
        return "account/login";
    }

    // 进行登录,注意， @Valid 为结构体，form表单的传值需要用这个方式
    @PostMapping("/account/login")
    public String login(@Valid Account account , HttpSession session, Model model,BindingResult bindingResult){
        logger.info("进行登录");
        if(bindingResult.hasErrors()){
            return bindingResult.getFieldError().getDefaultMessage();
        }

        account = accountService.getAccount(account);

        if(account != null){

            session.setAttribute("account",account);
            model.addAttribute("account",account);
            logger.info(account.getUsername()+"登录成功");
            return "catalog/main";
        }else{
            // 清空account
            model.addAttribute("account",account);
            logger.info("登录失败");
            return "account/login";
        }

    }


    // 进行注销
    @GetMapping("/account/signOut")
    public String signOut(Model model,HttpSession session){

        Account account = (Account)session.getAttribute("account");
        logger.info(account.getUsername()+"进行注销");

        account = null;

        session.setAttribute("account",account);
        model.addAttribute("account",account);
        return "catalog/main";
    }

    // 跳往注册界面
    @GetMapping("/account/viewRegisterForm")
    public String viewRegisterForm(Model model,HttpSession session){
        logger.info("跳往注册界面");

        List<String> languages = new ArrayList<String>();
        languages.add("English");
        languages.add("中文");
        session.setAttribute("languages",languages);

        List<String> categories = new ArrayList<>();
        categories.add("FISH");
        categories.add("DOGS");
        categories.add("REPTILES");
        categories.add("CATS");
        categories.add("BIRDS");
        session.setAttribute("categories",categories);

        return "account/register";
    }

    // 进行注册
    @PostMapping("/account/register")
    public String register(Account account, Model model) {
        logger.info("进行注册");
        if(!(account.getUsername().equals("") || account.getPassword().equals(""))){
            if(account.getPassword().equals(account.getRepeatedPassword())){
                Account temp = accountService.getAccount(account.getUsername());

                // 若用户名可创建则注册用户
                if (temp == null) {
                    accountService.insertAccount(account);
                    // 这里要清空model的account?
                    Account t = null;
                    model.addAttribute("account",t);
                    logger.info("注册成功");
                    return "account/login";
                }
            }

        }

        // 这里要清空model的account?
        Account t = null;
        model.addAttribute("account",t);
        logger.info("注册失败");
        return "account/register";
    }

    // 更改用户信息
    @GetMapping("/account/editAccount")
    public String editAccount(HttpSession session){
        logger.info("更改用户信息");
        List<String> languages = new ArrayList<String>();
        languages.add("english");
        languages.add("japanese");
        session.setAttribute("languages",languages);

        List<String> categories = new ArrayList<>();
        categories.add("FISH");
        categories.add("DOGS");
        categories.add("REPTILES");
        categories.add("CATS");
        categories.add("BIRDS");
        session.setAttribute("categories",categories);

        return "account/editAccount";
    }

    // 确认修改用户信息
    @PostMapping("/account/confirmEdit")
    public String confirmEdit(@Valid Account account,HttpSession session,Model model){

        if(!account.getPassword().equals("") && !account.getRepeatedPassword().equals("") && account.getPassword().equals(account.getRepeatedPassword())){
            accountService.updateAccount(account);
            session.setAttribute("account",account);
            model.addAttribute("account",account);

            return "catalog/main";
        }else{
            logger.info("确认修改用户信息");
            return "account/editAccount";
        }

    }

    // AJAX 判断用户名是否存在
    @GetMapping("/account/usernameIsExist")
    public void usernameIsExist(@RequestParam("username") String username, HttpServletResponse response){

        //  为空就不做判断
        if(username.equals("")){
            return;
        }

        Account account = accountService.getAccount(username);

        try{
            response.setContentType("text/xml;charset=utf-8");
            PrintWriter out = response.getWriter();
            response.setHeader("Cache-Control", "no-cache");
            out.println("<?xml version='1.0' encoding='"+"utf-8"+"' ?>");

            if(account != null){
                out.println("<msg>Exist</msg>");
            }else{
                out.println("<msg>NotExist</msg>");
            }
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
