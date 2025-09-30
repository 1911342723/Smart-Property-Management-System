package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 系统操作日志实体类
 */
@ApiModel(description = "系统操作日志")
@TableName("sys_log")
public class SysLog extends BaseEntity {

    @ApiModelProperty(value = "操作模块")
    @TableField("module")
    private String module;

    @ApiModelProperty(value = "操作类型")
    @TableField("operation_type")
    private String operationType;

    @ApiModelProperty(value = "操作描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "请求方法")
    @TableField("method")
    private String method;

    @ApiModelProperty(value = "请求URL")
    @TableField("request_url")
    private String requestUrl;

    @ApiModelProperty(value = "请求参数")
    @TableField("request_params")
    private String requestParams;

    @ApiModelProperty(value = "响应结果")
    @TableField("response_result")
    private String responseResult;

    @ApiModelProperty(value = "操作用户ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "操作用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "操作IP")
    @TableField("ip_address")
    private String ipAddress;

    @ApiModelProperty(value = "操作地点")
    @TableField("location")
    private String location;

    @ApiModelProperty(value = "浏览器")
    @TableField("browser")
    private String browser;

    @ApiModelProperty(value = "操作系统")
    @TableField("os")
    private String os;

    @ApiModelProperty(value = "执行时长(毫秒)")
    @TableField("execution_time")
    private Long executionTime;

    @ApiModelProperty(value = "操作状态(0=失败 1=成功)")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "错误信息")
    @TableField("error_msg")
    private String errorMsg;

    // Getters and Setters
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(String responseResult) {
        this.responseResult = responseResult;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
