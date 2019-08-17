# netty-im

protobuf 请安装3.6.1版本。 因为过于新的版本生成的java代码会报错。

```shell script
protoc --version
libprotoc 3.6.1
```
执行如下命令可以根据message.proto来生成MessageProto.java文件
```text
cd "fmessage.proto path"
protoc --java_out=. message.proto

```

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
