package NettyServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SerialHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof List) {
            System.out.println("list");
        } else {
            System.out.println("not list");
        }
    }
}