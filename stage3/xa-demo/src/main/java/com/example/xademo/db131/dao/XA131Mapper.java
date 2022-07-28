package com.example.xademo.db131.dao;

import com.example.xademo.db131.model.XA131;
import com.example.xademo.db131.model.XA131Example;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XA131Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xa_131
     *
     * @mbg.generated Wed Oct 02 17:01:02 CST 2019
     */
    long countByExample(XA131Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xa_131
     *
     * @mbg.generated Wed Oct 02 17:01:02 CST 2019
     */
    int deleteByExample(XA131Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xa_131
     *
     * @mbg.generated Wed Oct 02 17:01:02 CST 2019
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xa_131
     *
     * @mbg.generated Wed Oct 02 17:01:02 CST 2019
     */
    int insert(XA131 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xa_131
     *
     * @mbg.generated Wed Oct 02 17:01:02 CST 2019
     */
    int insertSelective(XA131 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xa_131
     *
     * @mbg.generated Wed Oct 02 17:01:02 CST 2019
     */
    List<XA131> selectByExample(XA131Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xa_131
     *
     * @mbg.generated Wed Oct 02 17:01:02 CST 2019
     */
    XA131 selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xa_131
     *
     * @mbg.generated Wed Oct 02 17:01:02 CST 2019
     */
    int updateByExampleSelective(@Param("record") XA131 record, @Param("example") XA131Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xa_131
     *
     * @mbg.generated Wed Oct 02 17:01:02 CST 2019
     */
    int updateByExample(@Param("record") XA131 record, @Param("example") XA131Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xa_131
     *
     * @mbg.generated Wed Oct 02 17:01:02 CST 2019
     */
    int updateByPrimaryKeySelective(XA131 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xa_131
     *
     * @mbg.generated Wed Oct 02 17:01:02 CST 2019
     */
    int updateByPrimaryKey(XA131 record);
}
