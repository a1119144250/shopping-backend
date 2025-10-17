package com.xiaowang.shopping.datasource.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * MyBatis Plus 元数据自动填充处理器
 * 支持Date和LocalDateTime两种时间类型
 * 
 * @author Hollis
 */
public class DataObjectHandler implements MetaObjectHandler {

  @Override
  public void insertFill(MetaObject metaObject) {
    // 支持Date类型的时间字段（BaseEntity使用）
    this.setFieldValByNameIfNull("gmtCreate", new Date(), metaObject);
    this.setFieldValByNameIfNull("gmtModified", new Date(), metaObject);

    // 支持LocalDateTime类型的时间字段（新实体类使用）
    this.setFieldValByNameIfNull("createTime", LocalDateTime.now(), metaObject);
    this.setFieldValByNameIfNull("updateTime", LocalDateTime.now(), metaObject);

    // 其他默认值（仅在字段存在时设置）
    this.setFieldValByNameIfExists("deleted", 0, metaObject);
    this.setFieldValByNameIfExists("lockVersion", 0, metaObject);
  }

  /**
   * 当没有值的时候再设置属性，如果有值则不设置。主要是方便单元测试
   * 如果字段不存在，静默忽略
   * 
   * @param fieldName  字段名
   * @param fieldVal   字段值
   * @param metaObject 元对象
   */
  private void setFieldValByNameIfNull(String fieldName, Object fieldVal, MetaObject metaObject) {
    // 先检查字段是否存在
    if (!metaObject.hasSetter(fieldName)) {
      return;
    }

    // 如果字段存在且值为null，则设置默认值
    if (metaObject.getValue(fieldName) == null) {
      this.setFieldValByName(fieldName, fieldVal, metaObject);
    }
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    // 支持Date类型的时间字段
    this.setFieldValByNameIfExists("gmtModified", new Date(), metaObject);

    // 支持LocalDateTime类型的时间字段
    this.setFieldValByNameIfExists("updateTime", LocalDateTime.now(), metaObject);
  }

  /**
   * 如果字段存在则设置值，字段不存在则静默忽略
   * 
   * @param fieldName  字段名
   * @param fieldVal   字段值
   * @param metaObject 元对象
   */
  private void setFieldValByNameIfExists(String fieldName, Object fieldVal, MetaObject metaObject) {
    if (metaObject.hasSetter(fieldName)) {
      this.setFieldValByName(fieldName, fieldVal, metaObject);
    }
  }
}
