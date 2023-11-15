package com.caipiao.config.handler;

import cn.hutool.core.util.StrUtil;
import com.caipiao.common.utils.AesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class AESEncryptHandler extends BaseTypeHandler {

    /**
     * 非空字段加密
     */
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, AesUtils.encrypt((String) parameter));
    }

    /**
     * 非空字段解密
     */
    @Override
    public Object getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        try {
            String columnValue = resultSet.getString(columnName);
            if(StrUtil.isNotBlank(columnValue)){
                return AesUtils.decrypt(columnValue);
            }
            return "";
        } catch (SQLException e) {
            log.error("typeHandler解密异常：" + e);
            return resultSet.getString(columnName);
        }
    }

    /**
     * 可空字段加密
     */
    @Override
    public Object getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        try {
            String columnValue = resultSet.getString(columnIndex);
            if(StrUtil.isNotBlank(columnValue)){
                return AesUtils.decrypt(columnValue);
            }
            return "";
        } catch (SQLException e) {
            log.error("typeHandler解密异常：" + e);
            return resultSet.getString(columnIndex);
        }
    }

    /**
     * 可空字段解密
     */
    @Override
    public Object getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        try {
            String columnValue = callableStatement.getString(columnIndex);
            if(StrUtil.isNotBlank(columnValue)){
                return AesUtils.decrypt(columnValue);
            }
            return "";
        } catch (SQLException e) {
            log.error("typeHandler解密异常：" + e);
            return callableStatement.getString(columnIndex);
        }
    }
}
