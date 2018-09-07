### 项目介绍

sysuQuery是一个完整的maven Java项目，用eclipse或IntelliJ IDEA打开即可

### 用到的资源文件
- mongoDB数据库
	- 主机+端口： ds223542.mlab.com:23542
	- 数据库名：  sysu
	- 用户名： sysu
	- 密码： sysu2018

可以使用MongoDB Compass可视化工具来浏览数据库内容

- sysu.owl文件,放在myData目录下

### 运行生成TDB数据库

在src/test/java/test/目录下，运行MongoDBConnectTest.java

生成的TDB数据库在DataBase/目录下