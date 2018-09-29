package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Netty中，通讯的双方建立连接后，会把数据按照ByteBuf的方式进行传输，
 * 例如http协议中，就是通过HttpRequestDecoder对ByteBuf数据流进行处理，转换成http的对象
 */
public class NettyServer {

    private static final Logger log= LoggerFactory.getLogger(NettyServer.class);

    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }
    EventLoopGroup bossGroup=new NioEventLoopGroup();
    EventLoopGroup workerGroup=new NioEventLoopGroup();
    //Channel channel;

    public void run() {
        ChannelFuture f=null;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer())
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            f =b.bind(port).sync();
            //channel=f.channel();
            f.channel().closeFuture().sync();
        }catch (Exception e) {
            log.error("Netty start error:", e);
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /*public void destroy(){
        log.info("Shutdown Netty Server...");
        if(channel != null) { channel.close();}
        //优雅退出，释放线程池资源
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
    }*/
    public static void main(String[] args)  throws Exception{
        NettyServer server=new NettyServer(8005);
        log.info("Http Server listening on 8005");
        server.run();
    }
}
