package com.example.demo.mapper;

import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecordMapper {

    /**
     * 根据 id 从数据库中查询对应的记录。
     */
    Record getRecordById(Integer id);

    /**
     * 根据 学号 从数据库中查询对应的记录。
     */
    List<Record> getRecordsByCollegeNum(String collegeNum);

    /**
     * @param record 待更新的对象
     * @return 如果更新成功返回 1 ，否则返回 0
     */
    int updateRecord(Record record);

    /**
     * @param id 待删除者的 id
     * @return 如果删除成功，返回 1，否则返回 0
     */
    int deleteRecordById(Integer id);

    /**
     * @param record 插入新记录.
     * @return 返回的是主键信息。
     */
    int insertRecord(Record record);

}
