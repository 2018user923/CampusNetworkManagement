package com.example.demo.mapper;

import com.example.demo.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 描述:  根据 id 查询用户信息
     *
     * @return 查询的用户信息
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:24
     * @param: id user.id
     */
    User getUserById(Integer id);

    /**
     * 描述: 根据类型查询用户信息
     *
     * @return 返回查询的集合
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:25
     * @param: type user.type
     */
    List<User> getUserByType(Integer type);


    /**
     * 描述:  根据用户名查询 userInfo
     *
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:26
     * @param: userName
     */
    User getUserByUserName(String userName);

    /**
     * 描述: 根据用户名和邮箱查询用户
     *
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:26
     * @param: userName
     * @param: email
     */
    User getUserByUserNameAndEmail(String userName, String email);

    /**
     * 描述: 更新用户信息
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:26
     * @param: user
     */
    int updateUser(User user);

    /**
     * 描述: 根据用户 id 更新用户信息
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 17:20
     * @param:
     */
    int updateUserById(User user);

    /**
     * 描述:  根据用户 id 删除用户
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:27
     * @param: id 待删除者的id
     */
    int deleteUserById(Integer id);

    /**
     * 描述: 根据用户名删除用户信息
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:28
     * @param: userName
     */
    int deleteUserByUserName(String userName);

    /**
     * 描述: 插入用户，这里需要注意的是，如果用户的某个属性为 null，也会被插入数据库中，所以调用该方法之前必须保证不存在为 null 的属性！
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:27
     * @param: user
     */
    int insertUser(User user);
}