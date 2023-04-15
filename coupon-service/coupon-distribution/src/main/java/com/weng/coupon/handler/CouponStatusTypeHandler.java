package com.weng.coupon.handler;

import com.weng.coupon.constants.CouponStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CouponStatusTypeHandler extends BaseTypeHandler<CouponStatus> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, CouponStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public CouponStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return CouponStatus.of(rs.getInt(columnName));
    }

    @Override
    public CouponStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return CouponStatus.of(rs.getInt(columnIndex));
    }

    @Override
    public CouponStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return CouponStatus.of(cs.getInt(columnIndex));
    }
}
