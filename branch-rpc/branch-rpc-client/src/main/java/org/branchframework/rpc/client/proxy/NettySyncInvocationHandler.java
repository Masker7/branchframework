package org.branchframework.rpc.client.proxy;

import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import org.branchframework.rpc.client.handler.RpcPromises;
import org.branchframework.rpc.core.protocol.message.RpcRequestMessage;

/**
 * 使用 Netty 的 Future 类进行同步调用
 * @author Haixin Wu
 * @since 1.0
 */
public class NettySyncInvocationHandler extends NettyInvocationHandler {

    public NettySyncInvocationHandler(String targetClassName, Channel channel, String methodVersion) {
        super(targetClassName, channel, methodVersion);
    }

    @Override
    public Object sendMessage(RpcRequestMessage message) {
        super.getChannel().writeAndFlush(message);
        DefaultPromise<Object> promise = new DefaultPromise<>(super.getChannel().eventLoop());
        RpcPromises.PROMISES.put(message.getSequenceId(), promise);

        try {
            // 同步等待结果
            promise.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (promise.isSuccess()) {
            return promise.getNow();
        } else {
            throw new RuntimeException(promise.cause());
        }
    }
}

