package netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.kurento.client.KurentoClient;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

public class NettyServerInitializer extends ChannelInitializer<Channel> {
    private SslContext context;
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //SSLEngine sslEngine=context.newEngine(ch.alloc());
        //pipeline.addLast("ssl",new SslHandler(sslEngine));

        //HttpServerCodec: 针对http协议进行编解码
        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        //ChunkedWriteHandler分块写处理，文件过大会将内存撑爆,WebSocket通信支持
        pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());

        pipeline.addLast("compressor",new HttpContentCompressor());//压缩
        /**
         * 作用是将一个Http的消息组装成一个完成的HttpRequest或者HttpResponse，那么具体的是什么
         * 取决于是请求还是响应, 该Handler必须放在HttpServerCodec后的后面
         */
        pipeline.addLast("aggegator", new HttpObjectAggregator(512 * 1024));//消息聚合,使用最大消息值是 512kb
        //TODO 心跳包
        //服务端如果长期没有收到客户端信息，就给客户端发送心跳包”ok”保持连接；如果服务器未收到客户端的反馈数据就主动断开客户端连接
        //这个就表示 如果60秒未收到客户端信息 ，服务端就主动断掉客户端； 如果15秒没有信息，服务器就向客户端 发送心跳信息
        //第一个参数   表示读操作空闲时间
        //第二个参数   表示写操作空闲时间
        //第三个参数   表示读写操作空闲时间
        //第四个参数 单位
        pipeline.addLast("ping", new IdleStateHandler(60, 20, 15, TimeUnit.SECONDS));
        //用于处理websocket, /ws为访问websocket时的uri
        pipeline.addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/ws"));
        //自定义业务
        pipeline.addLast("nettyHandler",new NettyServerHandller());
        //使得一个TCP在120秒内没有收到数据就断掉。
        pipeline.addLast(new ReadTimeoutHandler(120));
    }
}
