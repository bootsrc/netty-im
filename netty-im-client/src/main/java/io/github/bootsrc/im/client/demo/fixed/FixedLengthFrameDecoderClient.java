package io.github.bootsrc.im.client.demo.fixed;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class FixedLengthFrameDecoderClient {
	public static void main(String[] args) {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		Channel channel = null;
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new FixedLengthFrameDecoder(1500));
					ch.pipeline().addLast("decoder", new StringDecoder());
					ch.pipeline().addLast("encoder", new StringEncoder());
					ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
						@Override
					    public void channelRead(ChannelHandlerContext ctx, Object msg) {
							System.err.println("client:" + msg.toString());
					    }
					});
				}
			});

			ChannelFuture f = b.connect("127.0.0.1", 2222).sync();
			channel = f.channel();
			StringBuilder msg = new StringBuilder();
			for (int i = 0; i < 100; i++) {
				msg.append("hello yinjihuan");
			}
			System.out.println(msg.length());
			channel.writeAndFlush(msg);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
