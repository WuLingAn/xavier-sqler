# xavier-sqler

一个可以进行sql操作的工具类。

## 实现基础
使用yaml格式进行进行简单配置设计，直接适配hikaricp原生配置。

## 目标
- 希望方法使用足够简单，易于管理方式。
- 使用yml格式，考虑格式及解析相关√
- 基本sql功能，扩展多sql列表任务队列
- 实现多数据源配置√
- 实现事物管理

## 问题
连接的管理方式。目前有方案，为每个数据源，每个线程提供连接为同一个。
需要考虑事物管理的问题。

## TODO
- 完善测试用例。
- 测试h2数据库。
- 数据库连接的自动管理，万一能行呢。按照线程级别进行连接回收设计

## 可能不会做
应该不会做spring引入相关工作，因为是为了在非spring工程中使用才做的东西，spring环境下有更好的选择，我认为。