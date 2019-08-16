package io.github.bootsrc.im.client.handler;

import java.util.concurrent.TimeUnit;

import io.github.bootsrc.im.client.ImClientApp;
import io.github.bootsrc.im.client.core.ImConnection;
import io.github.bootsrc.im.core.message.MessageProto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ClientPoHandlerProto extends ChannelInboundHandlerAdapter {
	private ImConnection imConnection = new ImConnection();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		MessageProto.Message message = (MessageProto.Message) msg;
		System.out.println("client:" + message.getContent());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.err.println("掉线了...");
		//使用过程中断线重连
		final EventLoop eventLoop = ctx.channel().eventLoop();
		eventLoop.schedule(new Runnable() {
			@Override
			public void run() {
				imConnection.connect(ImClientApp.HOST, ImClientApp.PORT);
			}
		}, 1L, TimeUnit.SECONDS);
		super.channelInactive(ctx);
	}
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("长期没收到服务器推送数据,  说明跟服务器的连接已经端断开，需要重连服务器");
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                System.out.println("长期未向服务器发送数据");
                //发送心跳包
				MessageProto.Message heartBeatMsg = MessageProto.Message.newBuilder().setType(1).build();
                ctx.writeAndFlush(heartBeatMsg);
                System.out.println("Send heartbeat, heartBeatMsg=" + heartBeatMsg);
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
            	System.out.println("ALL");
            }
        }
    }
}
