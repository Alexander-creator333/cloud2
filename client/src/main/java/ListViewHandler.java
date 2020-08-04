import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

public class ListViewHandler extends ChannelInboundHandlerAdapter {

    private final CallBack callBack;

    public ListViewHandler(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        callBack.callBack(msg);
    }
}