create table if not exists ca
(
    id           int auto_increment comment 'CA ID'
        primary key,
    uuid         varchar(100)         not null comment 'CA UUID',
    privkey      text                 not null comment 'CA 私钥',
    cert         text                 not null comment 'CA 证书',
    parent_ca    varchar(100)         null comment '父 CA UUID（如果为根则应为空）',
    allow_sub_ca tinyint(1) default 1 null comment '是否允许此中间 CA签署下级 CA （根 CA 随便写）',
    owner        int                  not null comment '拥有者 ID',
    comment      varchar(500)         not null comment 'CA 别名',
    available    tinyint(1) default 1 not null comment '可用性',
    not_before   datetime             not null comment '在此之前不可用',
    not_after    datetime             not null comment '在此之后不可用',
    created_at   datetime             null comment '创建时间',
    modified_at  datetime             null comment '修改时间',
    deleted      tinyint(1) default 0 not null comment '是否被删除',
    constraint ca_pk
        unique (uuid)
)
    comment 'CA列表';

create table if not exists ca_binding
(
    id         int auto_increment comment '分配ID'
        primary key,
    ca_uuid    varchar(100) not null comment 'CA UUID',
    uid        int          not null comment '用户ID',
    created_at datetime     null comment '创建时间',
    constraint ca_alloc_pk_2
        unique (ca_uuid, uid)
)
    comment '分配 CA 用户';

create table if not exists certificate
(
    id          int auto_increment comment 'ID'
        primary key,
    uuid        varchar(100)         not null comment '证书 UUID ',
    privkey     text                 null comment '证书私钥',
    cert        text                 not null comment '证书',
    ca_uuid     varchar(100)         null comment '证书机构 UUID',
    owner       int                  not null comment '拥有者 ID',
    not_before  datetime             not null comment '在此之前不可用',
    not_after   datetime             not null comment '在此之后不可用',
    created_at  datetime             null comment '创建时间',
    modified_at datetime             null comment '修改时间',
    comment     varchar(500)         null comment '证书别名',
    deleted     tinyint(1) default 0 not null comment '是否被删除',
    constraint certficate_pk_2
        unique (uuid)
)
    comment 'SSL证书';

create table if not exists role
(
    id        int auto_increment comment '权限ID'
        primary key,
    role_name varchar(50) null comment '权限名称',
    constraint roles_pk_2
        unique (role_name)
)
    comment '权限';

create table if not exists role_binding
(
    id         int auto_increment comment 'id'
        primary key,
    uid        int      not null comment '用户ID',
    role_id    int      not null comment '权限ID',
    created_at datetime null comment '创建时间',
    constraint role_bindings_pk
        unique (uid, role_id)
)
    comment '权限绑定';

create table if not exists user
(
    id           int auto_increment comment 'user_id'
        primary key,
    username     varchar(50)          not null comment '用户名',
    display_name varchar(50)          not null,
    email        varchar(50)          not null comment '邮箱',
    password     varchar(100)         null comment '密码',
    role         int        default 1 null comment '角色',
    created_at   datetime             null comment '创建时间',
    modified_at  datetime             null comment '修改时间',
    deleted      tinyint(1) default 0 not null comment '是否被删除',
    constraint users_names
        unique (username)
)
    comment '用户';

