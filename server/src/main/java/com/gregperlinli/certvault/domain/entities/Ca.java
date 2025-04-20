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
 * CA列表
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
@Getter
@Setter
@ToString
@TableName("ca")
public class Ca implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * CA ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * CA UUID
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 签名算法
     */
    @TableField("algorithm")
    private String algorithm;

    /**
     * 密钥长度
     */
    @TableField("key_size")
    private Integer keySize;

    /**
     * CA 私钥
     */
    @TableField("privkey")
    private String privkey;

    /**
     * CA 证书
     */
    @TableField("cert")
    private String cert;

    /**
     * 父 CA UUID
     */
    @TableField("parent_ca")
    private String parentCa;

    /**
     * 是否允许创建子 CA
     */
    @TableField("allow_sub_ca")
    private Boolean allowSubCa;

    /**
     * 拥有者 ID
     */
    @TableField("owner")
    private Integer owner;

    /**
     * CA 别名
     */
    @TableField("comment")
    private String comment;

    /**
     * 可用性
     */
    @TableField("available")
    private Boolean available;

    /**
     * 在此之前不可用
     */
    @TableField("not_before")
    private LocalDateTime notBefore;

    /**
     * 在此之后不可用
     */
    @TableField("not_after")
    private LocalDateTime notAfter;

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
     * 是否被删除
     */
    @TableField("deleted")
    private Boolean deleted;
}
