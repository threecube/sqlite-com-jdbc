## 关于该工程
该工程是java与sqlite_com_dll(c#工程)的中间层，实现将sqlite_com_dll工程的dll库SqliteCOM2Dll.dll注册为COM组件，并将接口进行包装，实现对sqlite数据库的操作。

## 使用例子
#### 假如sqlite数据库文件为sqliteDB.db， 密码为123456， 其中有一张表t_test_table。
首先创建对应的DO对象：
```
@Data
@TableName("t_test_table")  //DO类对应的表名
public class TestTableDO {
	
	private Integer id;
	
	@TableColumn(name="printer_queue_Id")  //对应的表字段名
	private Integer printerQueueId;
	
	@TableColumn(scale=2)
	private double price;
	
	@TableColumn(name="new_price", scale=2)
	private BigDecimal newPrice;
}
```
#### 执行查询操作

```
# 创建sqliteDriver对象
SqliteDriver sqliteDriver = SqliteDriver.createInstance("C:\\Users\\sqliteDB.db", "123456");

# 生成select语句(可选)
String selectSql = SqliteDmlHelper.genSelectAll(TestTableDO.class);

# 执行查询操作获取json列表
List<JSONObject> jsonList = sqliteDriver.executeQuery(selectSql);

# 或者，执行查询操作获取DO列表
List<TestTableDO> testTableDOList = sqliteDriver.executeQuery(selectSql, TestTableDO.class);
```

## 使用步骤

### 1、引入jar包
使用如下命令将该工程的jar加载到本地仓库中
> 该工程的jar未上传到公共的maven仓库，因此建议， 将该jar放在您的工程的lib目录下，使用时首先在lib目录中运行如下命令。 

```
> mvn install:install-file -DgroupId=com.dinapin.orderdish -DartifactId=sqlite_com_jdbc -Dversion=0.0.1 -Dpackaging=jar -Dfile=sqlite_com_jdbc-0.0.1.jar
```

### 2、创建表的DO类
首先创建DO类，并在DO类中使用注解的方式指定对应的表名和字段名。jar提供了两种注解，分别是 ***@TableName***和 ***@TableColumn*** 。

#### 2.1、注解@TableColumn
用于DO对象的属性上，通过**name**指明属性对应的表字段名，通过**scale**指明精度。
##### *如果未指定**name**，则默认使用DO类的属性名作为表字段名*。 
##### *如果未指定**scale**， 则不进行精度处理；非double，float和BigDecimal类型的字段的scale设置，不进行精度处理*

使用方法如下：

```
# 只指定精度(默认对应的字段名就是price，取精度为小数点后2位)
@TableColumn(scale=2)
private double prince;

# 只指定对应的表字段名，不进行精度设置
@TableColumn(name="new_price")
private double newPrince;

# 只指定对应的表字段名，并且取精度为小数点后2位
@TableColumn(name="new_price", scale=2)
private BigDecimal newPrince;
```

#### 2.2、注解@TableName
用于DO类上，指定该DO类对应的表名， **该注解必须设置**。

### 3、初始化SqliteDriver
运行如下命令初始化SqliteDriver， 所有的sqlite数据库操作方法都由SqliteDriver提供：

```
SqliteDriver sqliteDriver = SqliteDriver.createInstance(String dbFilePath, String dbPassword);
```

>该类的初始化并不会创建sqlite数据库的连接，因此不必担心连接一直占用问题。

### 4、执行增删改查操作

#### 开启事务

```
sqliteDriver.beginTransaction();
```

#### 提交事务

```
sqliteDriver.commit();
```

#### 回滚事务

```
sqliteDriver.rollback();
```

#### 执行查询操作，返回json

```
# 以json的形式返回，params为可选项，如果params不为空，则使用params对querySql进行格式化
List<JSONObject> jsonList = sqliteDriver.executeQuery(String querySql, Object... params);
```

#### 执行查询操作，返回DO对象

```
# 以DO对象的形式返回，clazz指明DO类， params为可选项，如果params不为空，则使用params对querySql进行格式化
List<?> jsonList = sqliteDriver.executeQuery(String querySql, Class<?> clazz, Object...params);
```

#### 执行增删改操作

```
int result = sqliteDriver.execute(String nonQuerySql, Object...params);
```

### 4、Dml生成器
jar提供了dml生成器，可以为您生成简单的sql语句。

#### 生成Insert语句

```
# 入参为DO对象
String sql = SqliteDmlHelper.genInsert(T object);
```

#### 生成全表查询的sql， 返回所有字段的值

```
# 入参为DO的class
String sql = SqliteDmlHelper.genSelectAll(Class<?> clazz);
```

#### 生成条件查询的sql，返回所有字段的值

```
# conditions为查询条件，clazz为DO类
String sql = SqliteDmlHelper.genSelectAllWithWhere(Map<String, Object> conditions, Class<?> clazz);
```
#### 生成条件查询的sql，返回指定的字段

```
# columns为查询的字段列表， conditions为查询条件，clazz为DO类， 将从DO类对应的表中查询
String sql = SqliteDmlHelper.genSelectColumnsWithWhere(List<String> columns, Map<String, Object> conditions, Class<?> clazz);
```

#### 全表查询， 值返回指定的列

```
# columns为查询的字段列表，clazz为DO类， 将从DO类对应的表中查询
String sql = SqliteDmlHelper.genSelectColumns(List<String> columns, Class<?> clazz);
```

#### 生成update的sql语句

```
# updateColumns为更新的列和更新的值，conditions为更新条件， clazz为DO类；
String sql = SqliteDmlHelper.genUpdateWithWhere(Map<String, Object> updateColumns, Map<String, Object> conditions, Class<?> clazz);
```


## 开发帮助

### 该工程依赖jacob.jar， 因此需要先引入该jar
```
# 进入lib目录
> mvn install:install-file -DgroupId=com.jacob -DartifactId=jacob -Dversion=0.0.1 -Dpackaging=jar -Dfile=jacob.jar
```
### 发布该工程
##### 使用如下命令进行打包

```
> mvn assembly:assembly -Dmaven.test.skip=true
```
##### 重命名jar包
将**sqlite_com_jdbc-0.0.1-jar-with-dependencies.jar**重命名为**sqlite_com_jdbc-0.0.1.jar**进行发布。

## 可能问题

#### Could not load file or assembly 'Newtonsoft.Json, Version=11.0.0.0, Culture=neutral, PublicKeyToken=30ad4fe6b2a6aeed' or one of its dependencies. 系统找不到指定的文件。

可能原因：
* 没有将对应的dll加入到全局缓存中。使用***gacutil -if [库名].dll*** 将dll加载到全局缓存。
* 全局缓存的dll对应的版本不对，查看*C:\Windows\Microsoft.NET\assembly\GAC_MSIL\[库名]\[版本]* 查看当前缓存的dll版本. 如果版本不是需要的版本，重新将正确的版本加载到全局缓存。