package com.example.demo.mapper;

import com.example.demo.domain.Record;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecordMapper {

    /**
     * 根据 id 从数据库中查询对应的记录。
     */
    Record getRecordById(Integer id);

    /**
     * 根据 userName 获取所有 records
     */
    List<Record> getRecordsByUserName(String userName);

    /**
     * 分页获取 record
     */
    List<Record> getRecordsByUserNameForPages(String userName, Integer start, Integer limit, Integer type);

    /**
     * 根据 type 获取所有的 record
     */
    List<Record> getRecordsByType(Integer type);

    /**
     * 根据 userName 和 type 来查找记录
     */
    List<Record> getRecordsByUserNameAndType(String userName, Integer type);

    /**
     * 更新 record ,如果更新成功返回 1 ，否则返回 0
     */
    int updateRecordById(Integer id);

    /**
     * 根据 userName 更新 record
     */
    int updateRecordByUserName(String userName);

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
