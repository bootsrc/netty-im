package io.github.bootsrc.im.server.handler;

import io.github.bootsrc.im.core.message.MessageProto;
import io.github.bootsrc.im.server.core.ConnectionPool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerPoHandlerProto extends ChannelInboundHandlerAdapter {
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
		MessageProto.Message message = (MessageProto.Message) msg;
		if (ConnectionPool.getChannel(message.getId()) == null) {
			ConnectionPool.putChannel(message.getId(), ctx);
		}
		System.err.println("server:" + message.getId());
		// ping
		if (message.getType() == 1) {
			ctx.writeAndFlush(message);
		}
		
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
