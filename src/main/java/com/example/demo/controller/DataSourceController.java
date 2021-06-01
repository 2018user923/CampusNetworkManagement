package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.service.HttpService;
import com.example.demo.service.RecordsService;
import com.example.demo.service.UserService;
import com.example.demo.util.DBInputInfo;
import com.example.demo.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
public class DataSourceController {
    @Resource
    private UserService userService;

    @Resource
    private HttpService httpService;

    @Resource
    private RecordsService recordsService;

    /**
     * 描述:  用户提交的充值申请处理
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:08
     * @param: request
     * @param: map
     */
    @CrossOrigin
    @PostMapping("/userRechargeSubmit")
    ResultResponse userRechargeSubmit(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return userService.userRechargeAppHandler(request, Integer.valueOf(map.get("rechargeAmount")));
    }

    /**
     * 描述: 用户更新信息处理
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:09
     * @param: request
     * @param: user
     * @param: file
     */
    @CrossOrigin
    @PostMapping("/userInfoUpdateSubmit")
    ResultResponse userInfoUpdateSubmit(HttpServletRequest request, User user, @RequestParam(value = "file", required = false) MultipartFile file) {
        return userService.userInfoUpdateHandler(request, user, file);
    }

    /**
     * @Description 获取用户的基本信息
     * @Author <247702560@qq.com>
     * @Date 2021/5/31 23:00
     * @Param [request]
     */
    @CrossOrigin
    @PostMapping("/getUserInfo")
    User getUserInfo(HttpServletRequest request) {
        return userService.getUserInfoHandler(request);
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @Description 获取当前 ip 使用的流量数据
     * @Author <247702560@qq.com>
     * @Date 2021/5/31 23:00
     * @Param [request]
     */
    @CrossOrigin
    @RequestMapping("/getNetworkTraffic")
    Map<String, Object> getNetworkTraffic(HttpServletRequest request) {
        return httpService.getNetworkTrafficHandler(request);
    }

    /**
     * @return java.lang.String
     * @Description 注册账户邮箱发送验证码
     * @Author <247702560@qq.com>
     * @Date 2021/5/31 23:00
     * @Param [request, map]
     */
    @CrossOrigin
    @RequestMapping("/register/sendEmail")
    String registerSendEmail(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return httpService.registerSendEmail(request, map.get("email"));
    }

    /**
     * 描述: 登录账户邮箱发送验证码
     *
     * @return todo 返回 200 即可,这里后续需要修改！当前这里是返回 succeed
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 13:58
     * @param: request
     * @param: map 前端传入的数据，邮箱
     */
    @CrossOrigin
    @RequestMapping("/login/sendEmail")
    String loginSendEmail(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return httpService.loginSendEmail(request, map.get("email"));
    }

    /**
     * 描述: 返回当前登录用户的权限
     *
     * @return 返回的是当前用户的权限，结果是 set
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:01
     * @param: request
     */
    @CrossOrigin
    @RequestMapping("/getUserAuthorityList")
    Set<Integer> getUserAuthorityList(HttpServletRequest request) {
        return userService.getUserAuthorityListHandler(request);
    }

    /*
     * 描述: 根据传入的数据，来进行查询 records.
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:02
     * @param: request
     * @param: dbInputInfo
     */
    @CrossOrigin
    @PostMapping("/getRecords")
    ResultResponse getRecords(HttpServletRequest request, @RequestBody DBInputInfo dbInputInfo) {
        return recordsService.getRecordsHandler(request, dbInputInfo);
    }

    /**
     * 描述: 获取用户登录之后的申请信息
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:08
     * @param: request
     */
    @CrossOrigin
    @PostMapping("/getRecordsForLogIn")
    ResultResponse getRecordsForLogIn(HttpServletRequest request) {
        return recordsService.getRecordsForLogInHandler(request);
    }

    /**
     * 描述:  处理用户使用邮箱登录。
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:07
     * @param: request
     * @param: map 包含用户登录的用户名、邮箱、邮箱验证码等信息.
     */
    @CrossOrigin
    @RequestMapping("/login/emailLogin")
    String loginEmailLogin(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return userService.loginByEmailHandler(request, map.get("email"), map.get("code"), map.get("userName")) ? "successed" : "failed";
    }

    /**
     * 描述: 用户登录请求处理
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:06
     * @param: request
     * @param: user 用户登录信息
     */
    @CrossOrigin
    @RequestMapping("/login/userLogin")
    ResultResponse loginUserLogin(HttpServletRequest request, @RequestBody User user) {
        return userService.loginUserLoginHandler(request, user);
    }

    /**
     * 描述: 用户注册请求的逻辑处理，不能够创建管理员用户！
     *
     * @return 返回注册成功的信息，如果注册成功，那么会再次调用登录处理程序。
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:05
     * @param: request
     * @param: map 包含了传教用户的基本信息，及其邮箱验证码。
     */
    @CrossOrigin
    @RequestMapping("/register/userRegister")
    ResultResponse userRegister(HttpServletRequest request, @RequestBody Map<String, String> map) {
        User user = User.builder()
                .userName(map.get("userName"))
                .passWord(map.get("passWord"))
                .email(map.get("email"))
                .build();
        String code = map.get("code");
        return userService.userRegisterHandler(request, user, code);
    }

    /**
     * 描述: 用户取消申请按钮的请求处理
     *
     * @return 返回是否处理成功。成功返回 200
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:02
     * @param: request
     * @param: id 是 record 记录的主键
     */
    @CrossOrigin
    @RequestMapping("/buttons/cancelRecord/{id}")
    ResultResponse cancelRecord(@PathVariable("id") Integer id) {
        return recordsService.cancelRecordHandler(id);
    }

    /**
     * 描述: 用户再次提交申请按钮的请求处理
     *
     * @return 返回是否处理成功。成功返回 200
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:02
     * @param: request
     * @param: id 是 record 记录的主键
     */
    @CrossOrigin
    @RequestMapping("/buttons/repeatedSubmitRecord/{id}")
    ResultResponse repeatedSubmitRecord(@PathVariable("id") Integer id) {
        return recordsService.repeatedSubmitHandler(id);
    }

    /**
     * 描述: 用户删除提交申请按钮的请求处理
     *
     * @return 返回是否处理成功。成功返回 200
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:02
     * @param: request
     * @param: id 是 record 记录的主键
     */
    @CrossOrigin
    @RequestMapping("/buttons/deleteRecord/{id}")
    ResultResponse deleteRecord(@PathVariable("id") Integer id) {
        return recordsService.deleteSubmitHandler(id);
    }

    /**
     * 描述: 管理员同意提交申请按钮的请求处理
     *
     * @return 返回是否处理成功。成功返回 200
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:02
     * @param: request
     * @param: id 是 record 记录的主键
     */
    @CrossOrigin
    @RequestMapping("/buttons/agreeRecord/{id}")
    ResultResponse agreeRecord(HttpServletRequest request, @PathVariable("id") Integer id) {
        return recordsService.agreeRecordHandler(request, id);
    }

    /**
     * 描述: 管理员驳回提交申请按钮的请求处理
     *
     * @return 返回是否处理成功。成功返回 200
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:02
     * @param: request
     * @param: id 是 record 记录的主键
     */
    @CrossOrigin
    @RequestMapping("/buttons/turnDownRecord/{id}")
    ResultResponse turnDownRecord(HttpServletRequest request, @PathVariable("id") Integer id) {
        return recordsService.turnDownRecordHandler(request, id);
    }
}
