-- tc0x TESTS

drop table tc0x_sample
go
create table tc0x_sample (
  id      int          not null,
  value1  varchar(200) not null,
  value2  varchar(200) null
)
go
create unique index tc0x_sample_pk on tc0x_sample ( id )
go
grant all on tc0x_sample to test
go

drop table tc0x_race
go
create table tc0x_race (
  id      int          not null,
  value1  int          not null
)
go
create unique index tc0x_race_pk on tc0x_race ( id )
go
grant all on tc0x_race to test
go


-- tc1x TESTS

drop table tc1x_sample
go
create table tc1x_sample (
  id      int          not null,
  value1  varchar(200) not null,
  value2  varchar(200) null
)
go
create unique index tc1x_sample_pk on tc1x_sample ( id )
go
grant all on tc1x_sample to test
go

drop table tc1x_persist
go
create table tc1x_persist (
  id         integer         not null,
  ctime      datetime        not null,
  mtime      datetime        null,
  value1     varchar(200)    not null,
  parent_id  integer         null,
  group_id   numeric(10,0)   not null
)
go
create unique index tc1x_persist_pk on tc1x_persist ( id )
go
grant all on tc1x_persist to test
go

drop table tc1x_related
go
create table tc1x_related (
  id          integer     not null,
  persist_id  integer     not null
)
go
create unique index tc1x_related_pk on tc1x_related ( id )
go
grant all on tc1x_related to test
go

drop table tc1x_group
go
create table tc1x_group (
  id      numeric(10,0)  not null,
  value1  varchar(200)   not null
)
go
create unique index tc1x_group_pk on tc1x_group ( id )
go
grant all on tc1x_group to test
go

drop table tc1x_handling
go
create table tc1x_handling (
  id              numeric(10,0)  not null,
  tdt             datetime       not null,
  ttm             smalldatetime  not null,
  int_val         integer        null,
  float_val       float          null,
  real_val        real           null,
  long_val        numeric(18,0)  null,
  char_val        char(1)        null,
  bool_val        char(1)        null,
  bool_is_method  char(1)        null,
  int_date        integer        null,
  str_time        char(23)       null,
  num_date        numeric(17,0)  null,
  date_str        datetime       null,
  long_date       numeric(20,0)  null
)
go
create unique index tc1x_handling_pk on tc1x_handling (id)
go
grant all on tc1x_handling to test
go

drop table tc1x_lob
go
create table tc1x_lob (
  id         numeric(10,0)  not null,
  blob_val   image          null,
  clob_val   text           null,
  blob_val2  image          null,
  clob_val2  text           null
)
go
create unique index tc1x_lob_pk on tc1x_lob ( id )
go
grant all on tc1x_lob to test
go

drop table tc1x_conv
go
create table tc1x_conv (
    id                 int          not null,
    bool_byte          int          null,
    bool_short         int          null,
    bool_short_minus   int          null,
    bool_int           int          null,
    bool_int_minus     int          null,
    bool_bigdec        numeric      null,
    bool_bigdec_minus  numeric      null,
    byte_int           int          null,
    short_int          int          null,
    long_int           int          null,
    double_int         int          null,
    float_int          float        null,
    byte_bigdec        numeric      null,
    short_bigdec       numeric      null,
    int_bigdec         numeric      null,
    float_bigdec       numeric      null,
    double_bigdec      numeric      null,
    short_string       varchar(20)  null,
    byte_string        varchar(20)  null,
    int_string         varchar(20)  null,
    long_string        varchar(20)  null,
    bigdec_string      varchar(20)  null,
    float_string       varchar(20)  null,
    double_string      varchar(20)  null
)
go
create unique index tc1x_conv_pk on tc1x_conv( id )
go
grant all on tc1x_conv to test
go

drop table tc1x_serial
go
create table tc1x_serial (
  id      integer        not null,
  dep     image           null
)
go
create unique index tc1x_serial_pk on tc1x_serial( id )
go
grant all on tc1x_serial to test
go

drop table if exists tc1x_rollback
go
create table tc1x_rollback (
  id      numeric(10,0) not null,
  value1  varchar(200)  not null,
  value2  varchar(200),
  numb     numeric(10,0)
)
go
create unique index tc1x_rollback_pk on tc1x_rollback ( id )
go
grant all on tc1x_rollback to test
go

drop table tc1x_pks_person
go
create table tc1x_pks_person (
  fname  varchar(100)    not null,
  lname  varchar(100)    not null,
  bday   datetime
)
go
create unique index tc1x_pks_person_pk on tc1x_pks_person( fname, lname )
go
grant all on tc1x_pks_person to test
go

drop table tc1x_pks_employee
go
create table tc1x_pks_employee (
  fname       varchar(100)    not null,
  lname       varchar(100)    not null,
  start_date  datetime        null
)
go
create unique index tc1x_pks_employee_pk on tc1x_pks_employee( fname, lname )
go
grant all on tc1x_pks_employee to test
go

drop table tc1x_pks_payroll
go
create table tc1x_pks_payroll (
  fname        varchar(100)    not null,
  lname        varchar(100)    not null,
  id           int             not null,
  holiday      int,
  hourly_rate  int
)
go
create unique index tc1x_pks_payroll_fk on tc1x_pks_payroll( fname, lname )
go
create unique index tc1x_pks_payroll_pk on tc1x_pks_payroll( id )
go
grant all on tc1x_pks_payroll to test
go

drop table tc1x_pks_address
go
create table tc1x_pks_address (
  fname   varchar(100)  not null,
  lname   varchar(100)  not null,
  id      int           not null,
  street  varchar(30)   null,
  city    varchar(30)   null,
  state   varchar(2)    null,
  zip     varchar(6)    null
)
go
create unique index tc1x_pks_address_pk on tc1x_pks_address( id )
go
grant all on tc1x_pks_address to test
go

drop table tc1x_pks_contract
go
create table tc1x_pks_contract (
  fname        varchar(100)    not null,
  lname        varchar(100)    not null,
  policy_no    int             not null,
  contract_no  int             not null,
  c_comment    varchar(100)    null
)
go
create unique index tc1x_pks_contract_fk on tc1x_pks_contract( fname, lname )
go
create unique index tc1x_pks_contract_pk on tc1x_pks_contract( policy_no, contract_no )
go
grant all on tc1x_pks_contract to test
go

drop table tc1x_pks_category_contract
go
create table tc1x_pks_category_contract (
  policy_no    int      not null,
  contract_no  int      not null,
  cate_id      int      not null
)
go
grant all on tc1x_pks_category_contract to test
go

drop table tc1x_pks_category
go
create table tc1x_pks_category (
  id    int              not null,
  name  varchar(100)     not null
)
go
create unique index tc1x_pks_category_pk on tc1x_pks_category( id )
go
grant all on tc1x_pks_category to test
go


-- tc2x TESTS

drop table tc2x_master
go
create table tc2x_master (
  id        numeric(10,0)  not null,
  value1    varchar(200)   not null,
  group_id  numeric(10,0)  null
)
go
create unique index tc2x_master_pk on tc2x_master ( id )
go
grant all on tc2x_master to test
go

drop table tc2x_detail
go
create table tc2x_detail (
  detail_id  numeric(10,0)  not null,
  master_id  numeric(10,0)  not null,
  value1     varchar(200 )  not null
)
go
create unique index tc2x_detail_pk on tc2x_detail ( detail_id )
go
grant all on tc2x_detail to test
go

drop table tc2x_detail2
go
create table tc2x_detail2
(
  detail2_id  numeric(10,0)  not null,
  detail_id   numeric(10,0)  not null,
  value1      varchar(200 )  not null
)
go
create unique index tc2x_detail2_pk on tc2x_detail2 ( detail2_id )
go
grant all on tc2x_detail2 to test
go

drop table tc2x_detail3
go
create table tc2x_detail3 (
  detail3_id  numeric(10,0)  not null,
  detail_id   numeric(10,0)  not null,
  value1      varchar(200 )  not null
)
go
create unique index tc2x_detail3_pk on tc2x_detail3 ( detail3_id )
go
grant all on tc2x_detail3 to test
go

drop table tc2x_group
go
create table tc2x_group (
  id      numeric(10,0)  not null,
  value1  varchar(200)   not null
)
go
create unique index tc2x_group_pk on tc2x_group ( id )
go
grant all on tc2x_group to test
go

drop table tc2x_depend2
go
drop table tc2x_depend_master
go
drop table tc2x_depend1
go
create table tc2x_depend1 (
  id          int not null primary key
)
go
grant all on tc2x_depend1 to test
go
create table tc2x_depend_master (
  id          int not null primary key,
  depend1_id  int,
  index tc2x_master_depend1 ( depend1_id ),
  foreign key ( depend1_id ) references tc2x_depend1 ( id )
)
go
grant all on tc2x_depend_master to test
go
create table tc2x_depend2 (
  id          int not null primary key,
  master_id   int,
  index tc2x_depend2_master ( master_id ),
  foreign key ( master_id ) references tc2x_depend_master ( id )
)
go
grant all on tc2x_depend2 to test
go

drop table tc2x_keygen
go
create table tc2x_keygen (
  id   int          not null,
  attr varchar(200) not null
)
go
create unique index tc2x_keygen_pk on tc2x_keygen ( id )
go
grant all on tc2x_keygen to test
go

drop table tc2x_keygen_ext
go
create table tc2x_keygen_ext (
  id   int          not null,
  ext  varchar(200) not null
)
go
create unique index tc2x_keygen_ext_pk on tc2x_keygen_ext ( id )
go
grant all on tc2x_keygen_ext to test
go

drop table tc2x_uuid
go
create table tc2x_uuid (
  id    char(30)      not null,
  attr  varchar(200)  not null
)
go
create unique index tc2x_uuid_pk on tc2x_uuid ( id )
go
grant all on tc2x_uuid to test
go

drop table tc2x_uuid_ext
go
create table tc2x_uuid_ext (
  id   char(30)     not null,
  ext  varchar(200) not null
)
go
create unique index tc2x_uuid_ext_pk on tc2x_uuid_ext ( id )
go
grant all on tc2x_uuid_ext to test
go

drop table tc2x_identity
go
create table tc2x_identity (
  id    numeric(10,0) identity,
  attr  varchar(200) not null
)
go
grant all on tc2x_identity to test
go

drop table tc2x_identity_ext
go
create table tc2x_identity_ext (
  id   numeric(10,0) not null,
  ext  varchar(200) not null
)
go
create unique index tc2x_ident_ext_pk on tc2x_identity_ext ( id )
go
grant all on tc2x_identity_ext to test
go

drop table tc2x_seqtable
go
create table tc2x_seqtable (
  table_name varchar(200) not null,
  max_id     int          null
)
go
create unique index tc2x_seqtable_pk on tc2x_seqtable ( table_name )
go
grant all on tc2x_seqtable to test
go











-- UNDEFINED TESTS

-- test_table
drop table test_table
go
create table test_table (
  id      int          not null,
  value1  varchar(200) not null,
  value2  varchar(200) null
)
go
create unique index test_table_pk on test_table ( id )
go
grant all on test_table to test
go

-- test many to many
drop table test_group_person
go
drop table test_many_group
go
drop table test_many_person
go

create table test_many_group (
  gid       int           not null,
  value1    varchar(100)  not null
)
go
create unique index test_many_group_pk on test_many_group ( gid )
go
grant all on test_many_group to test
go

create table test_many_person (
   pid      int          not null,
   value1   varchar(100) not null,
   helloworld varchar(100) null,
   sthelse varchar(100) null
)
go
create unique index test_many_person_pk on test_many_person ( pid )
go
grant all on test_many_person to test
go

create table test_group_person (
  gid int         not null,
  pid int        not null,
  CONSTRAINT person_delete
    FOREIGN KEY(pid) 
    REFERENCES test_many_person(pid),
  CONSTRAINT group_delete
    FOREIGN KEY(gid) 
    REFERENCES test_many_group(gid)
)
go
create index test_group_person_p_pk on test_group_person ( pid )
go
create index test_group_person_g_pk on test_group_person ( gid )
go
grant all on test_group_person to test
go

-- test multiple pk
drop table test_pks_person
go
create table test_pks_person (
  fname varchar(100)    not null,
  lname varchar(100)    not null,
  bday  datetime
)
go
create unique index test_pks_person_pk on test_pks_person( fname, lname )
go
grant all on test_pks_person to test
go

drop table test_pks_employee
go
create table test_pks_employee (
  fname varchar(100)    not null,
  lname varchar(100)    not null,
  start_date datetime null
)
go
create unique index test_pks_person_employee_pk on test_pks_employee( fname, lname )
go
grant all on test_pks_employee to test
go

drop table test_pks_payroll
go
create table test_pks_payroll (
  fname varchar(100)    not null,
  lname varchar(100)    not null,
  id int               not null,
  holiday int,
  hourly_rate int
)
go
create unique index test_pks_payroll_fk on test_pks_payroll( fname, lname )
go
create unique index test_pks_payroll_pk on test_pks_payroll( id )
go
grant all on test_pks_payroll to test
go

drop table test_pks_project
go
create table test_pks_project (
  fname varchar(100)    not null,
  lname varchar(100)    not null,
  id    int             not null,
  name  varchar(100)
)
go
create unique index test_pks_project_pk on test_pks_project( id )
go
grant all on test_pks_payroll to test
go

drop table test_pks_address
go
create table test_pks_address (
  fname varchar(100)    not null,
  lname varchar(100)    not null,
  id int               not null,
  street varchar(30) null,
  city  varchar(30) null,
  state varchar(2) null,
  zip varchar(6) null
)
go
create unique index test_pks_address_pk on test_pks_address( id )
go
grant all on test_pks_address to test
go

drop table test_pks_contract
go
create table test_pks_contract (
  fname varchar(100)    not null,
  lname varchar(100)    not null,
  policy_no int        not null,
  contract_no int      not null,
  c_comment varchar(100)  null
)
go
create unique index test_pks_contract_fk on test_pks_contract( fname, lname )
go
create unique index test_pks_contract_pk on test_pks_contract( policy_no, contract_no )
go
grant all on test_pks_contract to test
go

drop table test_pks_category_contract
go
create table test_pks_category_contract (
  policy_no int        not null,
  contract_no int      not null,
  cate_id int          not null
)
go
grant all on test_pks_category_contract to test
go

drop table test_pks_category
go
create table test_pks_category (
  id  int              not null,
  name varchar(100)     not null
)
go
create unique index test_pks_category_pk on test_pks_category( id )
go
grant all on test_pks_category to test
go

-- base class
drop table test_rel_person
go
create table test_rel_person (
  pid    int             not null,
  fname varchar(100)    not null,
  lname varchar(100)    not null,
  bday  datetime
)
go
create unique index test_rel_person_pk on test_rel_person( pid )
go
grant all on test_rel_person to test
go

-- extend base class (person)
drop table test_rel_employee
go
create table test_rel_employee (
  pid    int             not null,
  start_date datetime null
)
go
create unique index test_rel_person_employee_pk on test_rel_employee( pid )
go
grant all on test_rel_employee to test
go

-- depends class of person
drop table test_rel_address
go
create table test_rel_address (
  pid    int             not null,
  id  int               not null,
  street varchar(30) null,
  city  varchar(30) null,
  state varchar(2) null,
  zip varchar(6) null
)
go
create index test_rel_address_fk on test_rel_address( pid )
go
create unique index test_rel_address_pk on test_rel_address( id )
go
grant all on test_rel_address to test
go

-- depend class of employee
drop table test_rel_payroll
go
create table test_rel_payroll (
  pid    int             not null,
  id int               not null,
  holiday int,
  hourly_rate int
)
go
create unique index test_rel_payroll_fk on test_rel_payroll( pid )
go
create unique index test_rel_payroll_pk on test_rel_payroll( id )
go
grant all on test_rel_payroll to test
go
-- end for test_relations

-- test_table_extends
drop table test_table_extends
go
create table test_table_extends (
  id      int          not null,
  value3  varchar(200) null,
  value4  varchar(200) null
)
go
create unique index test_table_extends_pk on test_table_extends ( id )
go
grant all on test_table_extends to test
go

-- test_table_ex
drop table test_table_ex
go
create table test_table_ex (
  id      int          not null,
  value1  varchar(200) not null,
  value2  varchar(200) null
)
go
create unique index test_table_ex_pk on test_table_ex ( id )
go
grant all on test_table_ex to test
go

-- test_group
drop table test_group
go
create table test_group (
  id     numeric(10,0)  not null,
  value1  varchar(200)   not null
)
go
create unique index test_group_pk on test_group ( id )
go
grant all on test_group to test
go

-- The test stored procedure on TransactSQL
drop procedure proc_check_permissions
go
create procedure proc_check_permissions @userName varchar(200),
                                        @groupName varchar(200) AS
    SELECT id, value1, value2 FROM test_table WHERE value1 = @userName
    SELECT id, value1, value2 FROM test_table WHERE value2 = @groupName
go
sp_procxmode proc_check_permissions, "anymode"
go

-- test_persistent
drop table test_persistent
go
create table test_persistent (
  id       integer          not null,
  ctime    datetime         not null,
  mtime    datetime         null,
  value1    varchar(200)    not null,
  parent_id integer         null,
  group_id numeric(10,0)    not null
)
go
create unique index test_persistent_pk on test_persistent ( id )
go
grant all on test_persistent to test
go

drop table test_related
go
create table test_related (
  id          integer     not null,
  persist_id  integer     not null
)
go
create unique index test_related_pk on test_related ( id )
go
grant all on test_related to test
go

-- test_col
drop table test_col
go
create table test_col (
  id       integer         not null,
  dum    integer    null
)
go
create unique index test_col_pk on test_col( id )
go
grant all on test_col to test
go

drop table test_item
go
create table test_item (
  iid       integer         not null,
  id      integer         null
)
go
create unique index test_item_pk on test_item( iid )
go
grant all on test_item to test
go

drop table test_comp_item
go
create table test_comp_item (
  iid       integer         not null,
  id      integer         not null
)
go
create unique index test_comp_item_pk on test_comp_item( iid )
go
grant all on test_comp_item to test
go


-- list_types
drop table list_types
go
create table list_types (
  o_char  CHAR         null,
  o_nchar NCHAR        null,
  o_varchar VARCHAR(10) null,
  o_nvarchar VARCHAR(10) null,
  o_text TEXT null,
  o_datetime datetime null,
  o_smalldatetime SMALLDATETIME null,
  o_binary BINARY(10) null,
  o_varbinary VARBINARY(10) null,
  o_image IMAGE null,
  o_int   INT null
)
go
grant all on list_types to test
go

drop table test_oqlext
go
create table test_oqlext (
  ident   integer         not null,
  ext     integer         not null
)
go
create unique index test_oqlext_pk on test_oqlext( ident )
go
grant all on test_oqlext to test
go


drop table test_nton_a
go

create table test_nton_a (
  id         varchar(20)      not null,
  status     int              not null
)
go

grant all on test_nton_a to test
go

drop table test_nton_b
go

create table test_nton_b (
  id         varchar(20)      not null,
  status     int              not null
)
go

grant all on test_nton_b to test
go

drop table enum_prod
go
create table enum_prod (
  id        int not null,
  name      varchar(200) not null,
  kind      varchar(200) not null
)
go

-- test objects for TestTransientAttribute 

drop table trans_master
go
create table trans_master (
  id        int not null,
  name      varchar(200) not null,
  propty1	int,
  propty2	int,
  propty3	int,
  ent2		int
)
go

drop table trans_child1
go
create table trans_child1 (
  id        int not null,
  descr     varchar(200) not null
)
go

drop table trans_child2
go
create table trans_child2 (
  id        int not null,
  entityOneId int not null,
  descr     varchar(200) not null
)
go

insert into trans_master (id, name, propty1, propty2, ent2) values (1, 'entity1', 1, 2, 1)
go
insert into trans_child1 (id, descr) values (1, 'description1')
go
insert into trans_child2 (id, descr, entityOneId) values (1, 'description1', 1)
go
insert into trans_child2 (id, descr, entityOneId) values (2, 'description2', 1)
go
insert into trans_child2 (id, descr, entityOneId) values (3, 'description3', 1)
go

-- tc9x TESTS

drop table poly_ordr
go
create table poly_ordr (
  id int not null,
  name varchar (20) not null
)
go

drop table poly_detail
go
create table poly_detail (
  id int not null,
  category varchar (20) not null,
  location varchar (20) not null
)
go

drop table poly_owner
go
create table poly_owner (
  id int not null,
  name varchar (20) not null,
  product int not null
)
go

drop table poly_prod
go
create table poly_prod (
  id        int not null,
  name      varchar(200) not null,
  detail	int not null,
  owner		int
)
go

drop table poly_computer
go
create table poly_computer (
  id   int not null,
  cpu  varchar(200) not null
)
go

drop table poly_laptop
go
create table poly_laptop (
  id   int not null,
  weight  int not null,
  resolution varchar(19) not null
)
go

drop table poly_server
go
create table poly_server (
  id   int not null,
  numberOfCPUs  int not null,
  support int not null
)
go

drop table poly_car
go
create table poly_car (
  id   int not null,
  kw   int not null,
  make  varchar(200) not null
)
go

drop table poly_truck
go
create table poly_truck (
  id   int not null,
  max_weight   int not null
)
go

drop table poly_prod_multi
go
create table poly_prod_multi (
  id1        int not null,
  id2        int not null,
  name      varchar(200) not null,
  detail	int not null
)
go

drop table poly_computer_multi
go
create table poly_computer_multi (
  id1   int not null,
  id2        int not null,
  cpu  varchar(200) not null
)
go

drop table poly_laptop_multi
go
create table poly_laptop_multi (
  id1   int not null,
  id2        int not null,
  weight  int not null,
  resolution varchar(19) not null
)
go

drop table poly_server_multi
go
create table poly_server_multi (
  id1   int not null,
  id2        int not null,
  numberOfCPUs  int not null,
  support int not null
)
go

drop table poly_order_product
go
create table poly_order_product (
  order_id	int not null,
  product_id int not null
)
go

drop table poly_table_m
go
create table poly_table_m (
  id	int not null,
  name	varchar(20) not null
)
go

drop table poly_table_n
go
create table poly_table_n (
  id	int not null,
  name	varchar(20) not null
)
go

drop table poly_m_n
go
create table poly_m_n (
  m_id	int not null,
  n_id int not null
)
go


insert into poly_detail (id, category, location) values (1, 'category 1', 'location 1')
go
insert into poly_detail (id, category, location) values (2, 'category 2', 'location 2')
go
insert into poly_detail (id, category, location) values (3, 'category 3', 'location 3')
go
insert into poly_detail (id, category, location) values (4, 'category 4', 'location 4')
go
insert into poly_detail (id, category, location) values (5, 'category 5', 'location 5')
go

insert into poly_prod (id, name, detail, owner) values (1, 'laptop 1', 1, 1)
go
insert into poly_computer (id, cpu) values (1, 'centrino')
go
insert into poly_laptop (id, weight, resolution) values (1, 2800, '1280')
go

insert into poly_prod (id, name, detail, owner) values (2, 'laptop 2', 2, 2)
go
insert into poly_computer (id, cpu) values (2, 'centrino')
go
insert into poly_laptop (id, weight, resolution) values (2, 2700, '1024')
go

insert into poly_prod (id, name, detail, owner) values (3, 'server 3', 3, 3)
go
insert into poly_computer (id, cpu) values (3, 'pentium 4')
go
insert into poly_server (id, numberOfCPUs, support) values (3, 4, 3)
go

insert into poly_prod (id, name, detail, owner) values (4, 'server 4', 4, 4)
go
insert into poly_computer (id, cpu) values (4, 'pentium 4')
go
insert into poly_server (id, numberOfCPUs, support) values (4, 16,5)
go

insert into poly_prod (id, name, detail, owner) values (5, 'truck 5', 5, 5)
go
insert into poly_car (id, kw, make) values (5, 60, 'make 5')
go
insert into poly_truck (id, max_weight) values (5, 4)
go

insert into poly_prod_multi (id1, id2, name, detail) values (1, 1, 'laptop 1', 1)
go
insert into poly_computer_multi (id1, id2, cpu) values (1, 1, 'centrino')
go
insert into poly_laptop_multi (id1, id2, weight, resolution) values (1, 1, 2800, '1280')
go

insert into poly_prod_multi (id1, id2, name, detail) values (2, 2, 'laptop 2', 2)
go
insert into poly_computer_multi (id1, id2, cpu) values (2, 2, 'centrino')
go
insert into poly_laptop_multi (id1, id2, weight, resolution) values (2, 2, 2700, '1024')
go

insert into poly_prod_multi (id1, id2, name, detail) values (3, 3, 'server 3', 3)
go
insert into poly_computer_multi (id1, id2, cpu) values (3, 3, 'pentium 4')
go
insert into poly_server_multi (id1,  id2, numberOfCPUs, support) values (3, 3, 4, 3)
go

insert into poly_prod_multi (id1, id2, name, detail) values (4, 4, 'server 4', 4)
go
insert into poly_computer_multi (id1, id2, cpu) values (4, 4, 'pentium 4')
go
insert into poly_server_multi (id1, id2, numberOfCPUs, support) values (4, 4, 16,5)
go

insert into poly_owner (id, name, product) values (1, 'owner 1', 1)
go
insert into poly_owner (id, name, product) values (2, 'owner 2', 2)
go
insert into poly_owner (id, name, product) values (3, 'owner 3', 3)
go
insert into poly_owner (id, name, product) values (4, 'owner 4', 4)
go
insert into poly_owner (id, name, product) values (5, 'owner 5', 5)
go

insert into poly_ordr (id, name) values (1, 'order 1')
go

insert into poly_order_product (order_id, product_id) values (1, 1)
go
insert into poly_order_product (order_id, product_id) values (1, 2)
go

insert into poly_m_n (m_id, n_id) values (1, 1)
go
insert into poly_m_n (m_id, n_id) values (1, 2)
go

insert into poly_table_m (id, name) values (1, "m1")
go
insert into poly_table_m (id, name) values (2, "m2")
go

insert into poly_table_n (id, name) values (1, "n1")
go
insert into poly_table_n (id, name) values (2, "n2")
go

drop tabel if exists poly_base
go
create table poly_base (
  id varchar(64) not null default '',
  color varchar(64) default null,
  primary key  (ID)
)
go

insert into poly_base values ('100','red')
go

drop table poly_derived
go
create table poly_derived (
  id varchar(64) not null default '',
  scent varchar(64) default null,
  primary key  (ID)
)
go
insert into poly_derived values ('100','vanilla')
go

drop table poly_container
go
create table poly_container (
  id varchar(64) not null default '',
  reference varchar(64) default null,
  primary key  (ID)
)
go
insert into poly_container values ('200','100')
go

drop table poly_Product
go
create table poly_Product(
  IdProd numeric(10) primary key,
  NameProd   varchar(30) null,
  DescProd   varchar(30) null
)
go

drop table poly_ActProduct
go
create table poly_ActProduct(
  IdAct numeric(10) primary key references Product (IdProd),
  BestSeason varchar(30) null
)
go

drop table poly_ComposedOffer
go
create table poly_ComposedOffer(
  IdCOffer numeric(10) primary key references Product (IdProd),
  NameCO   varchar(30) null,
  DescCO   varchar(30) null
)
go

drop table poly_OfferComposition
go
create table poly_OfferComposition(
  Offer numeric(10),
  Product numeric(10), 
  constraint unique_rel unique (Offer, Product) 
)
go

-- tables required for TestPolymorphismDependedndObject

DROP TABLE poly_extend_object
go
CREATE TABLE poly_extend_object (
  id            int NOT NULL default '0',
  description2  varchar(50) NOT NULL default '',
  PRIMARY KEY (id)
)
go

INSERT INTO poly_extend_object VALUES (1, 'This is the extended object.')
go

DROP TABLE poly_base_object
go
CREATE TABLE poly_base_object (
  id           int NOT NULL default '0',
  description  varchar(50) NOT NULL default '',
  saved        char(1) default '0',
  PRIMARY KEY (id)
)
go

INSERT INTO poly_base_object VALUES (1, 'This is the test object.', '0')
go

DROP TABLE poly_depend_object
go
CREATE TABLE poly_depend_object (
  id           int NOT NULL default '0',
  parentId           int NOT NULL default '0',
  description  varchar(50) NOT NULL default '',
  PRIMARY KEY (id)
)
go

INSERT INTO poly_depend_object VALUES(1, 1, 'This is a description')
go

	
# TC129 

DROP TABLE container
go
CREATE TABLE container (
  id int NOT NULL ,
  name varchar(200) NULL,
  prop int default NULL,
  PRIMARY KEY (id)
)
go

INSERT INTO container (id, name, prop) VALUES 
  (1,'Container 1',1),
  (2,'Container 2',2),
  (3,'Container 3',3),
  (4,'Container 4',4)
go

DROP TABLE container_item
go
CREATE TABLE container_item (
  id int NOT NULL,
  item int default DEFAULT NULL,
  value varchar(200) DEFAULT NULL,
  PRIMARY KEY (id)
)
go

INSERT INTO container_item (id, item, value) VALUES 
  (1,1,'Container item 1'),
  (2,2,'Container item 2'),
  (3,3,'Container item 3'),
  (4,4,'Container item 4'),
  (5,1,'Container item 5'),
  (6,2,'Container item 6'),
  (7,3,'Container item 7'),
  (8,4,'Container item 8')
go
	
# TC128a

drop table sorted_container
go
create table sorted_container (
  id        int not null,
  name      varchar(200) not null
)
go

drop table sorted_item
go
create table sorted_item(
  id        int not null,
  id_1		int not null,
  name      varchar(200) not null
)
go

insert into sorted_container(id, name) values (1, 'container 1')
go
insert into sorted_container(id, name) values (2, 'container 2')
go
insert into sorted_container(id, name) values (1, 'container 3')
go

insert into sorted_item (id, id_1, name) values (1, 1, 'container item 1')
go
insert into sorted_item (id, id_1, name) values (2, 1, 'container item 2')
go
insert into sorted_item (id, id_1, name) values (3, 2, 'container item 3')
go
	
