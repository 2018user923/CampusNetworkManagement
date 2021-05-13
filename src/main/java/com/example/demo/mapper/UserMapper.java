package com.example.demo.mapper;

import com.example.demo.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    /**
     * @param id 管理员的id
     * @return 从数据库中查询到的 User 对象.
     */
    User getUserById(Integer id);

    /**
     * @param user 待更新的对象
     * @return 如果更新成功返回 1 ，否则返回 0
     */
    int updateUser(User user);

    /**
     * @param id 待删除者的id
     * @return 如果删除成功，返回 1，否则返回 0
     */
    int deleteUserById(Integer id);

    /**
     * @param user 插入新用户.
     * @return 返回的是主键信息。
     */
    int insertUser(User user);

    /*根据用户名查询User*/
    User getUserByUserName(String userName);
}