create table test26_master (
  id        numeric(10,0)  not null,
  value1    varchar(200)   not null,
  group_id  numeric(10,0)  null
)
/
create unique index test26_master_pk on test26_master ( id )
/
create table test26_detail (
  detail_id  numeric(10,0)  not null,
  master_id  numeric(10,0)  not null,
  value1     varchar(200)   not null
)
/
create unique index test26_detail_pk on test26_detail ( detail_id )
/
create table test26_detail2 (
  detail2_id  numeric(10,0)  not null,
  detail_id   numeric(10,0)  not null,
  value1      varchar(200 )  not null
)
/
create unique index test26_detail2_pk on test26_detail2 ( detail2_id )
/
create table test26_detail3 (
  detail3_id  numeric(10,0)  not null,
  detail_id   numeric(10,0)  not null,
  value1      varchar(200 )  not null
)
/
create unique index test26_detail3_pk on test26_detail3 ( detail3_id )
/
create table test26_group (
  id      numeric(10,0)  not null,
  value1  varchar(200)   not null
)
/
create unique index test26_group_pk on test26_group ( id )
/