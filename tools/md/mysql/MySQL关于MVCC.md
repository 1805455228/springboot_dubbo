

# MCVV

## 0. MVCC 连环问答

- MVCC是什么？多版本并发控制

- MVCC怎么实现的？

  - 有一个列作为版本标识

- Mysql的MVCC和MVCC有何区别？

  - MySQL的MVCC并不是真正意义上的MVCC，MySQL其实还用了排它锁,因为修改的时候，会锁定当前记录
  - 所以MySQL的mvcc不仅仅单纯是乐观锁，还有悲观锁

- 原理是什么？

  - 普通MVCC：原理就是一个版本标识字段
  - Mysql MVCC ：
    1. 版本标识字段：最新更新成功的事务id、undo log回滚事务id、回滚标识
    2. 悲观锁中的排它锁

- 作用是什么？

  - 读不加锁，普通的SELECT请求不会加锁，提高了数据库的并发处理能力；
  - 借助MVCC，数据库可以实现RC（提交读），RR（可重复读）等隔离级别，用户可以查看当前数据的前一个或者前几个历史版本。保证了ACID中的I特性（隔离性）

- 当前读是什么？

  - 当前读即加锁读，读取记录的最新版本号，会加锁保证其他并发事物不能修改当前记录，直至释放锁。

  - 插入insert /更新update /删除delete操作默认使用当前读
  - 显示的为select语句加lock in share mode或for update的查询也采用当前读模式

- 快照读如何理解？

  快照读：不加锁，读取记录的快照版本，而非最新版本，使用MVCC机制，最大的好处是读取不需要加锁，读写不冲突

- 当前读和快照读有何区别？同上

- MVCC 与隔离级别的关系

  - mysql中MVCC只能实现RC RR其它的隔离级别不兼容

  - 提交读（RC）：事务开始，读取最新的已经被修改了的记录，事务中也是读取最新的已经被修改了的记录

  - 可重复读（RR）：事务开始，读取最新的已经被修改了的记录,作为本事务的记录仓库A，然后本事务内的所有提交都是基于当前事务的记录仓库A进行修改，所有的修改作为一个**链存储**


## 1.MySQL MVCC具体实现

- 1.1 隐藏列
  mysql在行都设置了默认列（对查询不可见），包含有 data_trx_id、data_roll_ptr、db_row_id、delete bit

  - db_row_id是在用户没设置聚集索引保留
  - delete bit 删除标志
  - data_trx_id 最近更新或创建 这条记录的 事务id
  - data_roll_ptr 回滚指针（也称之为删除事务id，在事务中查找查找上个版本的记录就靠这个指针，指向了undo log的地址，可以把同一个事务中的多个版本理解为链式关系）
- mysql利用最新更新成功的事务id、和回滚事务id实现MVCC
  - 事务id分为三种：当前事务id、最新更新成功的事务id、历史最晚事务id
- 事务

  - 最晚事务id>当前事务ID >最新更新成功事务id （说明在你执行的过程中有其它事务已经进入了）
  - 当前事务事务id>历史最晚事务id（说明你这个事务才是最晚的，才新建的事务，还没有被记录）
  - 当前事务事务id<最新更新成功事务id（不存在）
- MySQ排它锁具体实现流程：
  - 事务排它锁修改数据（就是说在RC或者RR级别下，如果一个事务A正在修改一个记录，其它事务B不能修改，只能等A提交或者回滚之后才能修改）
  - 修改完之后，更新隐藏字段的事务id为当前事务id， 记录undo log ，并且把回滚指针指向上一个版本的地址
  - 成功了说明都不做，失败了从undo log 回滚

## 2. CAS与mysql MVCC的区别

 比如说id=1的记录，name是A

- CAS：可以理解为我在修改前查询id=1的记录name否是原来的值A,如果是则更新，不是的话则自旋
  - CAS所以会出现ABA的问题（B把记录修改成C，C把记录再修改成A，那么A就认为它修改的时候记录没有被其它值修改，然后它修改了），具体请查看CAS讲解
- mysql MVCC 的修改是利用的行锁，是悲观锁，自己在修改的过程中，除非自己提交了事务（不管修改后的值有没有被其它事务看到），否则其它事务都不能进行修改

