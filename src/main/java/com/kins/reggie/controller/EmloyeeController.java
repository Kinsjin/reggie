package com.kins.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kins.reggie.common.R;
import com.kins.reggie.entity.Employee;
import com.kins.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmloyeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1、将页面提交的password进行MD5加密处理
        String password =  employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据提交的username进行查询数据库
       LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
       lambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
       Employee emp = employeeService.getOne(lambdaQueryWrapper);

       //3、没有查询到返回登录失败结果
        if(emp == null){
            return R.error("登录失败");
        }

        //4、密码比对，不一致返回失败登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登入失败");
        }

        //5、查看员工状态，禁用返回禁用结果
        if(emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
}
