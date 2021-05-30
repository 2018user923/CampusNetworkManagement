package com.example.demo.util;

import com.example.demo.domain.Record;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DBInputInfo {
    /*主键*/
    Integer id;
    /*用户名*/
    String userName;
    /*邮箱*/
    String email;
    /*类型*/
    List<Integer> types;

    /*起始位置*/
    Integer start;
    /*每页的限制*/
    Integer limit;
    /*登录时间*/
    Date signIn;
    /*退出时间*/
    Date signOut;
    /*更新者的名字*/
    String updateUserName;
    /*该记录的创建时间*/
    Date createTime;
    /*记录*/
    List<Record> records;
}
