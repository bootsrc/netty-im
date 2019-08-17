# netty-im

## 开发环境准备
protobuf建议安装3.6.1版本。 因为过于新的版本生成的java代码会报错。
检测是否已经安装了protobuf，命令如下
```shell script
protoc --version
libprotoc 3.6.1
```
如果打印出版本说明已经安装好了。

protobuf在mac上的安装步骤如下
```text
wget https://github.com/protocolbuffers/protobuf/releases/download/v3.6.1/protobuf-java-3.6.1.zip

unzip protobuf-java-3.6.1.zip
cd protobuf-3.6.1
# 把protobuf安装到自己指定的目录比如/Users/frank/program/protobuf
./configure --prefix=/Users/frank/program/protobuf

make install

```
设置protobuf环境变量
```text
vi ~/.bash_profile
# 在最后面添加两行
export PROTOBUF=/Users/frank/program/protobuf
export PATH=$PATH:$PROTOBUF/bin
```
执行 
```shell script
source ~/.bash_profile
```
 然后退出终端，重新进入ssh终端. 再次执行protoc --version 发现安装成功了。

执行如下命令可以根据message.proto来生成MessageProto.java文件
```shell script
# 进入netty-im-core模块中的message.proto文件所在的路径
cd "message.proto path"
protoc --java_out=. message.proto

```

## 原理解析
短线重连，使用Netty自带的IdleStateHandler的基本原理如下
```text
当客户端20秒没往服务端发送过数据，就会触发IdleState.WRITER_IDLE事件，这个时候客户端就向服务端发送一条心跳数据，跟业务无关，
只是心跳。服务端收到心跳之后就会回复一条消息，表示已经收到了心跳的消息，只要收到了服务端回复的消息，那么就不会触发
IdleState.READER_IDLE事件，如果触发了IdleState.READER_IDLE事件就说明服务端没有给客户端响应，这个时候可以选择重新连接。
```

参考链接见 [链接](http://cxytiandi.com/blog/detail/18044)

## 演示步骤
Step1 启动ImClientApp,  观察日志
```text
服务端链接不上，开始重连操作...
服务端链接不上，开始重连操作...
服务端链接不上，开始重连操作...
服务端链接不上，开始重连操作...
服务端链接不上，开始重连操作...

```
说明来一旦链接失败后会重连

Step2 等待几秒钟后，再启动ImServerApp,观察日志
```text
服务端链接成功...
长期未向服务器发送数据
Send heartbeat, heartBeatMsg=type: 1

client:

长期未向服务器发送数据
Send heartbeat, heartBeatMsg=type: 1
```

Step3, 关闭ImServerApp, 日志
```text
掉线了...
服务端链接不上，开始重连操作...
服务端链接不上，开始重连操作...
```

Step4, 启动ImServerApp，观察日志，发现又自动连接上了。
