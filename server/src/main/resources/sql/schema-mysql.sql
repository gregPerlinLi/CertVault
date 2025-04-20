-- CA Table
create table if not exists ca
(
    id           int auto_increment comment 'CA ID'
        primary key,
    uuid         varchar(100)                               not null comment 'CA UUID',
    algorithm    varchar(10)default 'RSA'                   comment '签名算法',
    key_size     int        default 2048                    comment '密钥长度',
    privkey      text                                       not null comment 'CA 私钥',
    cert         text                                       not null comment 'CA 证书',
    parent_ca    varchar(100)                               null comment '父 CA UUID（如果为根则应为空）',
    allow_sub_ca tinyint(1) default 1                       null comment '是否允许此中间 CA签署下级 CA （根 CA 随便写）',
    owner        int                                        not null comment '拥有者 ID',
    comment      varchar(500)                               not null comment 'CA 别名',
    available    tinyint(1) default 1                       not null comment '可用性',
    not_before   datetime   default '1970-01-01 00:00:00'   not null comment '在此之前不可用',
    not_after    datetime   default '1970-01-01 00:00:00'   not null comment '在此之后不可用',
    created_at   datetime   default '1970-01-01 00:00:00'   null comment '创建时间',
    modified_at  datetime   default '1970-01-01 00:00:00'   null comment '修改时间',
    deleted      tinyint(1) default 0                       not null comment '是否被删除',
    constraint ca_pk
        unique (uuid)
)
    comment 'CA列表';

-- CA Binding Table
create table if not exists ca_binding
(
    id         int auto_increment comment '分配ID'
        primary key,
    ca_uuid    varchar(100)                                 not null comment 'CA UUID',
    uid        int                                          not null comment '用户ID',
    created_at datetime     default '1970-01-01 00:00:00'   null comment '创建时间',
    constraint ca_alloc_pk_2
        unique (ca_uuid, uid)
)
    comment '分配 CA 用户';

-- Certificate Table
create table if not exists certificate
(
    id          int auto_increment comment 'ID'
        primary key,
    uuid        varchar(100)                                not null comment '证书 UUID ',
    algorithm   varchar(10) default 'RSA'                   comment '签名算法',
    key_size    int         default 2048                    comment '密钥长度',
    privkey     text                                        null comment '证书私钥',
    cert        text                                        not null comment '证书',
    ca_uuid     varchar(100)                                null comment '证书机构 UUID',
    owner       int                                         not null comment '拥有者 ID',
    not_before  datetime    default '1970-01-01 00:00:00'   not null comment '在此之前不可用',
    not_after   datetime    default '1970-01-01 00:00:00'   not null comment '在此之后不可用',
    created_at  datetime    default '1970-01-01 00:00:00'   null comment '创建时间',
    modified_at datetime    default '1970-01-01 00:00:00'   null comment '修改时间',
    comment     varchar(500)                                null comment '证书别名',
    deleted     tinyint(1)  default 0                       not null comment '是否被删除',
    constraint certificate_pk_2
        unique (uuid)
)
    comment 'SSL证书';

-- Role Table
create table if not exists role
(
    id        int auto_increment comment '权限ID'
        primary key,
    role_name varchar(50) null comment '权限名称',
    constraint roles_pk_2
        unique (role_name)
)
    comment '权限';

-- Role Binding Table
create table if not exists role_binding
(
    id          int auto_increment comment 'id'
        primary key,
    uid         int                                         not null comment '用户ID',
    role_id     int                                         not null comment '权限ID',
    created_at  datetime default '1970-01-01 00:00:00'      null comment '创建时间',
    constraint role_bindings_pk
        unique (uid, role_id)
)
    comment '权限绑定';

-- User Table
create table if not exists user
(
    id           int auto_increment comment 'user_id'
        primary key,
    username     varchar(50)                                not null comment '用户名',
    display_name varchar(50)                                not null,
    email        varchar(50)                                not null comment '邮箱',
    password     varchar(100)                               null comment '密码',
    role         int        default 1                       null comment '角色',
    created_at   datetime   default '1970-01-01 00:00:00'   null comment '创建时间',
    modified_at  datetime   default '1970-01-01 00:00:00'   null comment '修改时间',
    deleted      tinyint(1) default 0                       not null comment '是否被删除',
    constraint users_names
        unique (username)
)
    comment '用户';

-- Login Record Table
create table if not exists login_record
(
    id          int auto_increment comment '记录ID'
        primary key,
    uuid        varchar(100)                                    not null comment '会话 UUID',
    uid         int                                             not null comment '用户ID',
    session_id  varchar(100)                                    null comment '会话ID',
    ip          varchar(100)    default '0.0.0.0'               null comment 'IP地址',
    region      varchar(50)     default 'Unknown'               comment '国家/地区',
    province    varchar(50)     default 'Unknown'               comment '省份/州',
    city        varchar(50)     default 'Unknown'               comment '城市',
    browser     varchar(50)                                     null comment '浏览器',
    os          varchar(50)                                     null comment '操作系统',
    platform    varchar(50)                                     null comment '平台',
    login_time  datetime        default '1970-01-01 00:00:00'   not null comment '登录时间',
    online      tinyint(1)      default 1                       null comment '是否在线',
    constraint login_record_pk
        unique (uuid)
)
    comment '用户登录记录';
