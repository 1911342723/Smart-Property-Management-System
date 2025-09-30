package com.property.aspect;

import com.alibaba.fastjson2.JSON;
import com.property.annotation.OperationLog;
import com.property.entity.SysLog;
import com.property.mapper.SysLogMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 操作日志切面
 */
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private SysLogMapper sysLogMapper;

    /**
     * 定义切点：所有带有@OperationLog注解的方法
     */
    @Pointcut("@annotation(com.property.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        // 创建日志对象
        SysLog sysLog = new SysLog();
        sysLog.setModule(operationLog.module());
        sysLog.setOperationType(operationLog.operationType());
        sysLog.setDescription(operationLog.description());
        sysLog.setCreateTime(LocalDateTime.now());
        
        // 获取请求信息
        if (request != null) {
            sysLog.setRequestUrl(request.getRequestURI());
            sysLog.setMethod(request.getMethod());
            sysLog.setIpAddress(getIpAddress(request));
            sysLog.setBrowser(getBrowser(request));
            sysLog.setOs(getOs(request));
        }
        
        // 获取当前用户信息
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                sysLog.setUsername(authentication.getName());
                // 这里可以进一步获取用户ID
            }
        } catch (Exception e) {
            // 忽略异常
        }
        
        // 获取请求参数
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // 过滤掉HttpServletRequest、HttpServletResponse等对象
                StringBuilder params = new StringBuilder();
                for (Object arg : args) {
                    if (arg != null && !isFilterObject(arg)) {
                        params.append(JSON.toJSONString(arg)).append(" ");
                    }
                }
                sysLog.setRequestParams(params.toString().trim());
            }
        } catch (Exception e) {
            sysLog.setRequestParams("参数解析失败：" + e.getMessage());
        }
        
        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            
            // 记录成功
            sysLog.setStatus(1);
            
            // 记录响应结果（限制长度）
            try {
                String resultStr = JSON.toJSONString(result);
                if (resultStr.length() > 2000) {
                    resultStr = resultStr.substring(0, 2000) + "...";
                }
                sysLog.setResponseResult(resultStr);
            } catch (Exception e) {
                sysLog.setResponseResult("结果解析失败");
            }
            
        } catch (Throwable e) {
            // 记录失败
            sysLog.setStatus(0);
            sysLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            sysLog.setExecutionTime(executionTime);
            
            // 异步保存日志（避免影响业务性能）
            try {
                sysLogMapper.insert(sysLog);
            } catch (Exception e) {
                // 日志保存失败不影响业务
                System.err.println("保存操作日志失败：" + e.getMessage());
            }
        }
        
        return result;
    }

    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 对于多级代理，取第一个IP
        if (ip != null && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }

    /**
     * 获取浏览器信息
     */
    private String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        
        if (userAgent.contains("Edge")) {
            return "Edge";
        } else if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "IE";
        }
        return "Other";
    }

    /**
     * 获取操作系统信息
     */
    private String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        
        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac")) {
            return "Mac OS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        }
        return "Other";
    }

    /**
     * 判断是否需要过滤的对象
     */
    private boolean isFilterObject(Object o) {
        return o instanceof HttpServletRequest
                || o instanceof javax.servlet.http.HttpServletResponse
                || o instanceof org.springframework.web.multipart.MultipartFile
                || o instanceof javax.servlet.ServletRequest
                || o instanceof javax.servlet.ServletResponse;
    }
}



