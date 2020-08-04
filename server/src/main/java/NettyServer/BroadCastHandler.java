package NettyServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BroadCastHandler extends SimpleChannelInboundHandler<String> {

    private static ConcurrentLinkedDeque<SocketChannel> channels = new ConcurrentLinkedDeque<>();
    private String name;
    private static int cnt = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        cnt++;
        name = "user#" + cnt;
        System.out.println("Client: " + name + " connected!");
        channels.add((SocketChannel) ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        for (SocketChannel channel : channels) {
            // echo
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
            channel.writeAndFlush(format.format(new Date()) + "-[" + name + "]: " + s);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // disconnect
        System.out.println("client: " + name + " leave");
        channels.remove((SocketChannel) ctx.channel());
    }
}