-- CA Table
CREATE TABLE IF NOT EXISTS ca (
    id SERIAL PRIMARY KEY,
    uuid VARCHAR(100) NOT NULL,
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


-- Certificate Table
CREATE TABLE IF NOT EXISTS certificate (
                                           id SERIAL PRIMARY KEY,
                                           uuid VARCHAR(100) NOT NULL,
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
    CONSTRAINT certficate_pk_2 UNIQUE (uuid)
    );

COMMENT ON TABLE certificate IS 'SSL证书';


-- Role Table
CREATE TABLE IF NOT EXISTS role (
                                    id SERIAL PRIMARY KEY,
                                    role_name VARCHAR(50) UNIQUE,
    CONSTRAINT roles_pk_2 UNIQUE (role_name)
    );

COMMENT ON TABLE role IS '权限';


-- Role Binding Table
CREATE TABLE IF NOT EXISTS role_binding (
                                            id SERIAL PRIMARY KEY,
                                            uid INT NOT NULL,
                                            role_id INT NOT NULL,
                                            created_at TIMESTAMP DEFAULT '1970-01-01 00:00:00.000000',
                                            CONSTRAINT role_bindings_pk UNIQUE (uid, role_id)
    );

COMMENT ON TABLE role_binding IS '权限绑定';


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
COMMENT ON COLUMN "user".role IS '角色（默认1）';
COMMENT ON COLUMN "user".deleted IS '是否被删除';
