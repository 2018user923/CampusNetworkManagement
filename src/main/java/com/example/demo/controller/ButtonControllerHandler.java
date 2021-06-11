package com.example.demo.controller;

import com.example.demo.service.RecordsService;
import com.example.demo.service.UserService;
import com.example.demo.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 处理各种按钮的请求
 *
 * @author <247702560@qq.com>
 * @since 2021/6/1 14:35
 */
@Slf4j
@RestController
public class ButtonControllerHandler {
    @Resource
    private RecordsService recordsService;

    @Resource
    private UserService userService;

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
    @RequestMapping("/buttons/deleteRecord/{id}/{type}")
    ResultResponse deleteRecord(@PathVariable("id") Integer id, @PathVariable("type") Integer type) {
        return recordsService.deleteSubmitHandler(id, type);
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

    /**
     * 描述: 管理员将用户加入黑名单
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 16:45
     * @param: request
     * @param: id
     */
    @CrossOrigin
    @RequestMapping("/buttons/addBlackList/{id}")
    ResultResponse addBlackList(HttpServletRequest request, @PathVariable("id") Integer id) {
        return userService.addBlackListHandler(request, id);
    }

    /**
     * 描述:  管理员将用户移出黑名单
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 16:45
     * @param: request
     * @param: id
     */
    @CrossOrigin
    @RequestMapping("/buttons/rmBlackList/{id}")
    ResultResponse rmBlackList(HttpServletRequest request, @PathVariable("id") Integer id) {
        return userService.rmBlackListHandler(request, id);
    }
}
