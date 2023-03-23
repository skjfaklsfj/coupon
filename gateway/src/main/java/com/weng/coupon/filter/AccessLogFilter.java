package com.weng.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuulFilter {
    @Override
    protected Object cRun() {
        HttpServletRequest httpServletRequest = requestContext.getRequest();
        Long startTime = (Long) requestContext.get("startTime");
        String uri = httpServletRequest.getRequestURI();
        long durationTime = System.currentTimeMillis() - startTime;
        log.info("uri: {}, duration time: {}", uri, durationTime);
        return success();
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }
}
