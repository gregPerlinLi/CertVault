package com.gregperlinli.certvault.domain.entities;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * SSL证书
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
@Getter
@Setter
@ToString
@TableName("certificate")
public class Certificate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId("id")
    private Integer id;

    /**
     * 证书 UUID 
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 证书私钥
     */
    @TableField("privkey")
    private String privkey;

    /**
     * 证书
     */
    @TableField("cert")
    private String cert;

    /**
     * 拥有者 ID
     */
    @TableField("owner")
    private Integer owner;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    @TableField("modified_at")
    private LocalDateTime modifiedAt;

    /**
     * 证书别名
     */
    @TableField("comment")
    private String comment;

    /**
     * 是否被删除
     */
    @TableField("deleted")
    private Boolean deleted;
}
