# netty-im

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