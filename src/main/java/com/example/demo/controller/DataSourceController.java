package com.example.demo.controller;

import com.example.demo.service.RecordsService;
import com.example.demo.service.UserService;
import com.example.demo.util.DBInputInfo;
import com.example.demo.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 数据库处理
 *
 * @author <247702560@qq.com>
 * @since 2021/6/1 14:32
 */
@Slf4j
@RestController
public class DataSourceController {

    @Resource
    private RecordsService recordsService;

    @Resource
    private UserService userService;

    /*
     * 描述: 根据传入的数据，来进行查询相关信息，会返回对应的 title、button、data
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:02
     * @param: request
     * @param: dbInputInfo
     */
    @CrossOrigin
    @PostMapping("/getRecords")
    ResultResponse getListData(HttpServletRequest request, @RequestBody DBInputInfo dbInputInfo) {
        return recordsService.getListDataHandler(request, dbInputInfo);
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
     * todo 这里写错了，需要删除！！！
     * 描述: 根据 type 查询数据库中的用户信息,这里主要是用户处理 type = TypeEnum.checkUserInfo 的情况
     *
     * @return 返回查询的用户信息
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:42
     * @param: request
     */
    @CrossOrigin
    @RequestMapping("/DB/getUserInfoByType")
    ResultResponse getUserInfoByType(HttpServletRequest request, @RequestBody Integer type) {
        return userService.getUserInfoByTypeHandler(request, type);
    }

}
