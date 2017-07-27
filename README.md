# InstantMessaging
## 与Java的局域网内多人聊天工具
- 服务器端：服务器端主要负责处理用户的注册登录消息，并管理一个在线用户的列表，如果某个用户的状态发生了变化(有新用户上线或者有用户下线)，服务器端负责把这个用户的状态变化信息发送给其他所有的客户端。
- 客户端：客户端主要负责给用户提供注册、登录和聊天的界面，并接受服务器返回的消息作出相应的操作。

## 运行方式
### 服务器端
- 创建数据库
    因为本次课程设计的用户信息，包括账号和密码都是存在数据库中的，我们使用的是Mysql数据库，所以需要先在服务器主机上创建一个数据库和数据表，创建方法如下，其中要注意的是数据库的名字、表的名字和各字段的名称:
create database instantmessage;
create table users(username varchar(100) not null, password varchar(100) not null);
- 启动服务器
    启动服务器的时候需要指定4个参数，包括服务器用来与客户端连接的端口号、服务器端Mysql数据库的用户名、密码和端口。
    例如以下的启动方法，就是使用9876的端口与客户端连接，Mysql的用户名、密码和端口分别是root、mysql和3306。
    java -jar server.jar 9876 root mysql 3306
    

### 客户端
    与启动服务器类似，启动客户端需要提供2个参数，包括服务器端的IP和使用的端口。
    例如，可以按照以下的方式启动客户端。
    java -jar client.jar 127.0.0.1 9876


