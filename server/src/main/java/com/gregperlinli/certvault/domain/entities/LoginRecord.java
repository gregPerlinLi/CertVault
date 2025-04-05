package com.gregperlinli.certvault.domain.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 用户登录记录
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-04-05
 */
@Getter
@Setter
@ToString
@TableName("login_record")
public class LoginRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 会话UUID
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 用户ID
     */
    @TableField("uid")
    private Integer uid;

    /**
     * 登录会话ID
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 登录IP
     */
    @TableField("ip")
    private String ip;

    /**
     * 浏览器
     */
    @TableField("browser")
    private String browser;

    /**
     * 操作系统
     */
    @TableField("os")
    private String os;

    /**
     * 登录设备
     */
    @TableField("platform")
    private String platform;

    /**
     * 登录时间
     */
    @TableField("login_time")
    private LocalDateTime loginTime;

    /**
     * 是否在线
     */
    @TableField("online")
    private Boolean online;

}
