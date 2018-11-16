package com.yzsj.neteco.common.bean;

/**
 * 告警相关实体类
 * @author baimu
 * @date 2018-11-16
 * */
public class AlarmVO {
    /**
     * 告警流水号,告警唯一标识
     * */
    private String alarmSN;
    //告警名称
    private String alarmName;


    //首次发生的起始时间
    private Long startTime;

    //首次发生的截至时间
    private Long endTime;

    //告警源
    private String alarmSource;

    //告警定位信息
    private String location;

    //确认时间  单位ms
    private Integer ackTime;

    //确认用户
    private String ackUser;

    //是否已经确认
    private Boolean acked;

    //附加信息
    private String additionalInformation;

    //附加文本
    private  String additionalText;

    //告警标识  不能作为唯一标识
    private  Integer alarmId;

    //告警到达网管时间
    private String arrivedTime;

    //告警清除用户
    private String clearUser;

    //是否已经清除
    private Boolean cleared;

    //告警清除时间
    private Long clearedTime;

    // 清除类型：1：ADAC 自动检测自动清除。2：ADMC 自动检测手动清除。
    private Integer clearedType;

    //备注时间
    private Long  commentTime;

    //备注用户
    private String commentUser;

    //备注
    private String comments;

    //设备流水号
    private Integer devCsn;

    /**
     * 告警产生时间
     * */
    private Long eventTime;

    /**
     * 告警类型:
     *  1. 通信告警
     *  2：设备告警
     * 3：处理出错告警
     * 4：业务质量告警
     * 5：环境告警
     * 6：完整性告警
     * 7：操作告警
     * 8：物力资源告警
     * 9：安全告警
     * 10：时间域告警
     * */
    private Integer eventType;

    //最近一次发生时间
    private Long lastestLogTime;

    //管理对象
    private String moDn;

    //管理对象名称
    private String moName;

    //告警源标识
    private String neDN;

    //网元名称
    private String neName;

    //设备类型
    private String neType;

    //定位信息
    private String objectInstance;

    /**
     * 告警级别：
     * 0：不确定
     * 1：紧急
     * 2：重要
     * 3：次要
     * 4：提示
     * */
    private Integer perceivedSeverity;

    //告警可能原因
    private Integer probableCause;

    //告警可能原因描述信息
    private String probableCauseStr;

    //修复建议
    private String proposedRepairActions;

    /**
     * 告警组id
     * */
    private String alarmGroupId;

    public AlarmVO(String alarmSN, String alarmName, Long startTime, Long endTime, String alarmSource, String location, Integer ackTime, String ackUser, Boolean acked, String additionalInformation, String additionalText, Integer alarmId, String arrivedTime, String clearUser, Boolean cleared, Long clearedTime, Integer clearedType, Long commentTime, String commentUser, String comments, Integer devCsn, Long eventTime, Integer eventType, Long lastestLogTime, String moDn, String moName, String neDN, String neName, String neType, String objectInstance, Integer perceivedSeverity, Integer probableCause, String probableCauseStr, String proposedRepairActions, String alarmGroupId) {
        this.alarmSN = alarmSN;
        this.alarmName = alarmName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.alarmSource = alarmSource;
        this.location = location;
        this.ackTime = ackTime;
        this.ackUser = ackUser;
        this.acked = acked;
        this.additionalInformation = additionalInformation;
        this.additionalText = additionalText;
        this.alarmId = alarmId;
        this.arrivedTime = arrivedTime;
        this.clearUser = clearUser;
        this.cleared = cleared;
        this.clearedTime = clearedTime;
        this.clearedType = clearedType;
        this.commentTime = commentTime;
        this.commentUser = commentUser;
        this.comments = comments;
        this.devCsn = devCsn;
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.lastestLogTime = lastestLogTime;
        this.moDn = moDn;
        this.moName = moName;
        this.neDN = neDN;
        this.neName = neName;
        this.neType = neType;
        this.objectInstance = objectInstance;
        this.perceivedSeverity = perceivedSeverity;
        this.probableCause = probableCause;
        this.probableCauseStr = probableCauseStr;
        this.proposedRepairActions = proposedRepairActions;
        this.alarmGroupId = alarmGroupId;
    }

    public String getAlarmSN() {
        return alarmSN;
    }

    public void setAlarmSN(String alarmSN) {
        this.alarmSN = alarmSN;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getAlarmSource() {
        return alarmSource;
    }

    public void setAlarmSource(String alarmSource) {
        this.alarmSource = alarmSource;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getAckTime() {
        return ackTime;
    }

    public void setAckTime(Integer ackTime) {
        this.ackTime = ackTime;
    }

    public String getAckUser() {
        return ackUser;
    }

    public void setAckUser(String ackUser) {
        this.ackUser = ackUser;
    }

    public Boolean getAcked() {
        return acked;
    }

    public void setAcked(Boolean acked) {
        this.acked = acked;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getAdditionalText() {
        return additionalText;
    }

    public void setAdditionalText(String additionalText) {
        this.additionalText = additionalText;
    }

    public Integer getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Integer alarmId) {
        this.alarmId = alarmId;
    }

    public String getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(String arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public String getClearUser() {
        return clearUser;
    }

    public void setClearUser(String clearUser) {
        this.clearUser = clearUser;
    }

    public Boolean getCleared() {
        return cleared;
    }

    public void setCleared(Boolean cleared) {
        this.cleared = cleared;
    }

    public Long getClearedTime() {
        return clearedTime;
    }

    public void setClearedTime(Long clearedTime) {
        this.clearedTime = clearedTime;
    }

    public Integer getClearedType() {
        return clearedType;
    }

    public void setClearedType(Integer clearedType) {
        this.clearedType = clearedType;
    }

    public Long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Long commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getDevCsn() {
        return devCsn;
    }

    public void setDevCsn(Integer devCsn) {
        this.devCsn = devCsn;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public Long getLastestLogTime() {
        return lastestLogTime;
    }

    public void setLastestLogTime(Long lastestLogTime) {
        this.lastestLogTime = lastestLogTime;
    }

    public String getMoDn() {
        return moDn;
    }

    public void setMoDn(String moDn) {
        this.moDn = moDn;
    }

    public String getMoName() {
        return moName;
    }

    public void setMoName(String moName) {
        this.moName = moName;
    }

    public String getNeDN() {
        return neDN;
    }

    public void setNeDN(String neDN) {
        this.neDN = neDN;
    }

    public String getNeName() {
        return neName;
    }

    public void setNeName(String neName) {
        this.neName = neName;
    }

    public String getNeType() {
        return neType;
    }

    public void setNeType(String neType) {
        this.neType = neType;
    }

    public String getObjectInstance() {
        return objectInstance;
    }

    public void setObjectInstance(String objectInstance) {
        this.objectInstance = objectInstance;
    }

    public Integer getPerceivedSeverity() {
        return perceivedSeverity;
    }

    public void setPerceivedSeverity(Integer perceivedSeverity) {
        this.perceivedSeverity = perceivedSeverity;
    }

    public Integer getProbableCause() {
        return probableCause;
    }

    public void setProbableCause(Integer probableCause) {
        this.probableCause = probableCause;
    }

    public String getProbableCauseStr() {
        return probableCauseStr;
    }

    public void setProbableCauseStr(String probableCauseStr) {
        this.probableCauseStr = probableCauseStr;
    }

    public String getProposedRepairActions() {
        return proposedRepairActions;
    }

    public void setProposedRepairActions(String proposedRepairActions) {
        this.proposedRepairActions = proposedRepairActions;
    }

    public String getAlarmGroupId() {
        return alarmGroupId;
    }

    public void setAlarmGroupId(String alarmGroupId) {
        this.alarmGroupId = alarmGroupId;
    }
}
