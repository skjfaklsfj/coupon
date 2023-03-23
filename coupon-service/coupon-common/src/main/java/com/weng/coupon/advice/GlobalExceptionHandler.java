package com.weng.coupon.advice;

import com.weng.coupon.exception.CouponException;
import com.weng.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handleCouponException(HttpServletRequest request, CouponException exception) {
        CommonResponse<String> response = new CommonResponse<>(-1, "businessError");
        response.setData(exception.getMessage());
        return response;
    }
}
