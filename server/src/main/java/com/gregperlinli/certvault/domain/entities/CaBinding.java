package com.gregperlinli.certvault.domain.entities;

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
 * 分配 CA 用户
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
@Getter
@Setter
@ToString
@TableName("ca_binding")
public class CaBinding implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分配ID
     */
    @TableId("id")
    private Integer id;

    /**
     * CA ID 
     */
    @TableField("ca_id")
    private Integer caId;

    /**
     * 用户ID
     */
    @TableField("uid")
    private Integer uid;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
