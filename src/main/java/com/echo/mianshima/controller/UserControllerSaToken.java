package com.echo.mianshima.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.echo.mianshima.common.BaseResponse;
import com.echo.mianshima.common.ResultUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/user")
public class UserControllerSaToken {
    // 测试登录，浏览器访问： http://localhost:8101/api/test/user/doLogin?username=zhang&password=123456
    @RequestMapping("doLogin")
    public BaseResponse<String> doLogin(String username, String password) {
        // 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对
        if("zhang".equals(username) && "123456".equals(password)) {
            StpUtil.login(10001);
            return ResultUtils.success("登录成功");
        }
        return ResultUtils.success("登录失败");
    }

    // 查询登录状态，浏览器访问： http://localhost:8101/api/test/user/isLogin
    @RequestMapping("isLogin")
    public BaseResponse<String> isLogin() {
        return ResultUtils.success("当前会话是否登录：" + StpUtil.isLogin());
    }
}
