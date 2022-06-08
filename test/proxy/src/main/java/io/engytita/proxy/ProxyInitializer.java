package io.engytita.proxy;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.engytita.proxy.channel.BackendChannelBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class ProxyInitializer extends ChannelInitializer<Channel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyInitializer.class);

    private ProxyMaster master;

    public ProxyInitializer(ProxyConfig config) {
        this(new ProxyMaster(config, new BackendChannelBootstrap()));
    }

    public ProxyInitializer(ProxyMaster master) {
        this.master = master;
    }

    @Override
    protected void initChannel(Channel channel) {
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        Address clientAddress = new Address(address.getHostName(), address.getPort());

        ConnectionContext context = new ConnectionContext(master)
                .withClientAddr(clientAddress)
                .withClientChannel(channel)
                .withAlloc(channel.alloc());
        context.listener().onInit(context, channel);

        LOGGER.debug("{} : connection init", context);

        channel.pipeline().replace(this, null, context.proxyHandler());
        channel.pipeline().addLast(context.provider().tailFrontendHandler());
    }
}
