package com.weng.coupon.handler;

import com.google.gson.Gson;
import com.weng.coupon.vo.TemplateRule;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
@MappedJdbcTypes(JdbcType.VARCHAR)
public class TemplateRuleTypeHandler extends BaseTypeHandler<TemplateRule> {
    private Gson gson = new Gson();
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, TemplateRule templateRule, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, gson.toJson(templateRule));
    }

    @Override
    public TemplateRule getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return gson.fromJson(resultSet.getString(s), TemplateRule.class);
    }

    @Override
    public TemplateRule getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return gson.fromJson(resultSet.getString(i), TemplateRule.class);
    }

    @Override
    public TemplateRule getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return gson.fromJson(callableStatement.getString(i), TemplateRule.class);
    }
}
