-- CA Table
CREATE TABLE IF NOT EXISTS ca (
    id SERIAL PRIMARY KEY,
    uuid VARCHAR(100) NOT NULL,
    algorithm VARCHAR(10) DEFAULT 'RSA',
    key_size INT DEFAULT 2048,
    privkey TEXT NOT NULL,
    cert TEXT NOT NULL,
    parent_ca VARCHAR(100),
    allow_sub_ca BOOLEAN DEFAULT TRUE,
    owner INT NOT NULL,
    comment VARCHAR(500) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    not_before TIMESTAMP NOT NULL DEFAULT '1970-01-01 00:00:00.000000',
    not_after TIMESTAMP NOT NULL DEFAULT '1970-01-01 00:00:00.000000',
    created_at TIMESTAMP DEFAULT '1970-01-01 00:00:00.000000',
    modified_at TIMESTAMP DEFAULT '1970-01-01 00:00:00.000000',
    deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT ca_pk UNIQUE (uuid)
    );

COMMENT ON TABLE ca IS 'CA列表';
COMMENT ON COLUMN ca.algorithm IS '签名算法';
COMMENT ON COLUMN ca.key_size IS '密钥长度';
COMMENT ON COLUMN ca.uuid IS 'CA UUID';
COMMENT ON COLUMN ca.allow_sub_ca IS '是否允许此中间 CA签署下级 CA （根 CA 随便写）';
COMMENT ON COLUMN ca.available IS '可用性';
COMMENT ON COLUMN ca.deleted IS '是否被删除';


-- CA Binding Table
CREATE TABLE IF NOT EXISTS ca_binding (
    id SERIAL PRIMARY KEY,
    ca_uuid VARCHAR(100) NOT NULL,
    uid INT NOT NULL,
    created_at TIMESTAMP DEFAULT '1970-01-01 00:00:00.000000',
    CONSTRAINT ca_alloc_pk_2 UNIQUE (ca_uuid, uid)
    );

COMMENT ON TABLE ca_binding IS '分配 CA 用户';
COMMENT ON COLUMN ca_binding.ca_uuid IS 'CA UUID';
COMMENT ON COLUMN ca_binding.uid IS '用户ID';
COMMENT ON COLUMN ca_binding.created_at IS '创建时间';


-- Certificate Table
CREATE TABLE IF NOT EXISTS certificate (
    id SERIAL PRIMARY KEY,
    uuid VARCHAR(100) NOT NULL,
    algorithm VARCHAR(10) DEFAULT 'RSA',
    key_size INT DEFAULT 2048,
    privkey TEXT,
    cert TEXT NOT NULL,
    ca_uuid VARCHAR(100),
    owner INT NOT NULL,
    not_before TIMESTAMP NOT NULL DEFAULT '1970-01-01 00:00:00.000000',
    not_after TIMESTAMP NOT NULL DEFAULT '1970-01-01 00:00:00.000000',
    created_at TIMESTAMP DEFAULT '1970-01-01 00:00:00.000000',
    modified_at TIMESTAMP DEFAULT '1970-01-01 00:00:00.000000',
    comment VARCHAR(500),
    deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT certificate_pk_2 UNIQUE (uuid)
    );

COMMENT ON TABLE certificate IS 'SSL证书';
COMMENT ON COLUMN certificate.uuid IS '证书 UUID';
COMMENT ON COLUMN certificate.algorithm IS '签名算法';
COMMENT ON COLUMN certificate.key_size IS '密钥长度';
COMMENT ON COLUMN certificate.privkey IS '私钥';
COMMENT ON COLUMN certificate.cert IS '证书';
COMMENT ON COLUMN certificate.ca_uuid IS 'CA UUID';
COMMENT ON COLUMN certificate.owner IS '证书拥有者';
COMMENT ON COLUMN certificate.not_before IS '证书生效时间';
COMMENT ON COLUMN certificate.not_after IS '证书失效时间';
COMMENT ON COLUMN certificate.comment IS '备注';
COMMENT ON COLUMN certificate.deleted IS '是否被删除';

-- Role Table
CREATE TABLE IF NOT EXISTS role (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE,
    CONSTRAINT roles_pk_2 UNIQUE (role_name)
    );

COMMENT ON TABLE role IS '权限';
COMMENT ON COLUMN role.id IS '角色ID';
COMMENT ON COLUMN role.role_name IS '权限名';


-- Role Binding Table
CREATE TABLE IF NOT EXISTS role_binding (
    id SERIAL PRIMARY KEY,
    uid INT NOT NULL,
    role_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT '1970-01-01 00:00:00.000000',
    CONSTRAINT role_bindings_pk UNIQUE (uid, role_id)
    );

COMMENT ON TABLE role_binding IS '权限绑定';
COMMENT ON COLUMN role_binding.uid IS '用户ID';
COMMENT ON COLUMN role_binding.role_id IS '角色ID';
COMMENT ON COLUMN role_binding.created_at IS '创建时间';

-- User Table
CREATE TABLE IF NOT EXISTS "user" (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(100),
    role INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT '1970-01-01 00:00:00.000000',
    modified_at TIMESTAMP DEFAULT '1970-01-01 00:00:00.000000',
    deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT users_names UNIQUE (username)
    );

COMMENT ON TABLE "user" IS '用户';
COMMENT ON COLUMN "user".username IS '用户名';
COMMENT ON COLUMN "user".display_name IS '显示名';
COMMENT ON COLUMN "user".email IS '邮箱';
COMMENT ON COLUMN "user".password IS '密码';
COMMENT ON COLUMN "user".role IS '角色（默认1）';
COMMENT ON COLUMN "user".created_at IS '创建时间';
COMMENT ON COLUMN "user".modified_at IS '修改时间';
COMMENT ON COLUMN "user".deleted IS '是否被删除';

-- Login Record Table
CREATE TABLE IF NOT EXISTS login_record (
    id SERIAL PRIMARY KEY,
    uuid VARCHAR(100) NOT NULL,
    uid INT NOT NULL,
    session_id VARCHAR(100),
    ip VARCHAR(100) DEFAULT '0.0.0.0',
    region VARCHAR(50) DEFAULT 'Unknown',
    province VARCHAR(50) DEFAULT 'Unknown',
    city VARCHAR(50) DEFAULT 'Unknown',
    browser VARCHAR(50),
    os VARCHAR(50),
    platform VARCHAR(50),
    login_time TIMESTAMP DEFAULT '1970-01-01 00:00:00.000000',
    online BOOLEAN DEFAULT TRUE,
    CONSTRAINT login_record_pk UNIQUE (uuid)
    );

COMMENT ON TABLE login_record IS '登录记录';
COMMENT ON COLUMN login_record.id IS '记录ID';
COMMENT ON COLUMN login_record.uuid IS '记录UUID';
COMMENT ON COLUMN login_record.uid IS '用户ID';
COMMENT ON COLUMN login_record.session_id IS '会话ID';
COMMENT ON COLUMN login_record.ip IS 'IP地址';
COMMENT ON COLUMN login_record.region IS '国家/地区';
COMMENT ON COLUMN login_record.province IS '省份/州';
COMMENT ON COLUMN login_record.city IS '城市';
COMMENT ON COLUMN login_record.browser IS '浏览器';
COMMENT ON COLUMN login_record.os IS '操作系统';
COMMENT ON COLUMN login_record.platform IS '平台';
COMMENT ON COLUMN login_record.login_time IS '登录时间';
COMMENT ON COLUMN login_record.online IS '是否在线';
