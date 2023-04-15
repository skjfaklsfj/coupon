package com.weng.coupon.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.joda.time.DateTime;

import java.sql.*;

@MappedJdbcTypes(JdbcType.DATE)
public class JodaTimeTypeHandler extends BaseTypeHandler<DateTime> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, DateTime dateTime, JdbcType jdbcType) throws SQLException {
        preparedStatement.setDate(i, new Date(dateTime.getMillis()));
    }

    @Override
    public DateTime getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return new DateTime(resultSet.getDate(s));
    }

    @Override
    public DateTime getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return new DateTime(resultSet.getDate(i));
    }

    @Override
    public DateTime getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return new DateTime(callableStatement.getDate(i));
    }
}
