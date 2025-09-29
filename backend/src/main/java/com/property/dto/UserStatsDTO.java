package com.property.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户统计数据DTO
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "用户统计数据")
public class UserStatsDTO {

    @ApiModelProperty(value = "报修次数")
    private Integer repairCount;

    @ApiModelProperty(value = "缴费次数")
    private Integer paymentCount;

    @ApiModelProperty(value = "投诉建议数")
    private Integer complaintCount;

    @ApiModelProperty(value = "访客预约数")
    private Integer visitorCount;

    @ApiModelProperty(value = "活动参与数")
    private Integer activityCount;

    public Integer getRepairCount() {
        return repairCount;
    }

    public void setRepairCount(Integer repairCount) {
        this.repairCount = repairCount;
    }

    public Integer getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(Integer paymentCount) {
        this.paymentCount = paymentCount;
    }

    public Integer getComplaintCount() {
        return complaintCount;
    }

    public void setComplaintCount(Integer complaintCount) {
        this.complaintCount = complaintCount;
    }

    public Integer getVisitorCount() {
        return visitorCount;
    }

    public void setVisitorCount(Integer visitorCount) {
        this.visitorCount = visitorCount;
    }

    public Integer getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(Integer activityCount) {
        this.activityCount = activityCount;
    }
}

