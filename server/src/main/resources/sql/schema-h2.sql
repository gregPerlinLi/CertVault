create table if not exists "ca"
(
    id           int auto_increment primary key,
    uuid         varchar(100)                               not null,
    algorithm    varchar(10) default 'RSA',
    key_size     int        default 2048,
    privkey      text                                       not null,
    cert         text                                       not null,
    parent_ca    varchar(100)                               null,
    allow_sub_ca boolean     default true                   null,
    owner        int                                        not null,
    comment      varchar(500)                               not null,
    available    boolean     default true                   not null,
    not_before   timestamp   default '1970-01-01 00:00:00'  not null,
    not_after    timestamp   default '1970-01-01 00:00:00'  not null,
    created_at   timestamp   default '1970-01-01 00:00:00'  null,
    modified_at  timestamp   default '1970-01-01 00:00:00'  null,
    deleted      boolean     default false                  not null,
    constraint ca_pk unique (uuid)
);

create table if not exists "ca_binding"
(
    id         int auto_increment primary key,
    ca_uuid    varchar(100)                               not null,
    uid        int                                        not null,
    created_at timestamp     default '1970-01-01 00:00:00' null,
    constraint ca_alloc_pk_2 unique (ca_uuid, uid)
);

create table if not exists "certificate"
(
    id          int auto_increment primary key,
    uuid        varchar(100)                               not null,
    algorithm   varchar(10) default 'RSA',
    key_size    int         default 2048,
    privkey     text                                       null,
    cert        text                                       not null,
    ca_uuid     varchar(100)                               null,
    owner       int                                        not null,
    not_before  timestamp    default '1970-01-01 00:00:00' not null,
    not_after   timestamp    default '1970-01-01 00:00:00' not null,
    created_at  timestamp    default '1970-01-01 00:00:00' null,
    modified_at timestamp    default '1970-01-01 00:00:00' null,
    comment     varchar(500)                               null,
    deleted     boolean      default false                 not null,
    constraint certificate_pk_2 unique (uuid)
);

create table if not exists "role"
(
    id        int auto_increment primary key,
    role_name varchar(50) null,
    constraint roles_pk_2 unique (role_name)
);

create table if not exists "role_binding"
(
    id          int auto_increment primary key,
    uid         int                                        not null,
    role_id     int                                        not null,
    created_at  timestamp default '1970-01-01 00:00:00'   null,
    constraint role_bindings_pk unique (uid, role_id)
);

create table if not exists "user"
(
    id           int auto_increment primary key,
    username     varchar(50)                               not null,
    display_name varchar(50)                               not null,
    email        varchar(50)                               not null,
    password     varchar(100)                              null,
    role         int        default 1                      null,
    created_at   timestamp   default '1970-01-01 00:00:00' null,
    modified_at  timestamp   default '1970-01-01 00:00:00' null,
    deleted      boolean     default false                 not null,
    constraint users_names unique (username)
);

create table if not exists "login_record"
(
    id          int auto_increment primary key,
    uuid        varchar(100)                               not null,
    uid         int                                        not null,
    session_id  varchar(100)                               null,
    ip          varchar(100)    default '0.0.0.0'          null,
    region      varchar(50)     default 'Unknown',
    province    varchar(50)     default 'Unknown',
    city        varchar(50)     default 'Unknown',
    browser     varchar(50)                                  null,
    os          varchar(50)                                  null,
    platform    varchar(50)                                  null,
    login_time  timestamp        default '1970-01-01 00:00:00' not null,
    online      boolean          default true                null,
    constraint login_record_pk unique (uuid)
);