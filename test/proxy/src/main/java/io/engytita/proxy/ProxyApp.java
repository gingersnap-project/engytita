package io.engytita.proxy;

import io.engytita.proxy.listener.ProxyCacheListener;
import picocli.CommandLine;

/**
 * @since 14.0
 **/
@CommandLine.Command(name = "proxy", mixinStandardHelpOptions = true)
public class ProxyApp implements Runnable {

   @CommandLine.Option(names = {"-h", "--frontend-host"}, description = "The frontend host", defaultValue = "127.0.0.1")
   String frontendHost;
   @CommandLine.Option(names = {"-b", "--backend-host"}, description = "The backend host", defaultValue = "127.0.0.1")
   String backendHost;

   @CommandLine.Option(names = {"-s", "--backend-port"}, description = "The backend port", defaultValue = "8080")
   int backendPort;

   @CommandLine.Option(names = {"-p", "--frontend-port"}, description = "The frontend port", defaultValue = "9090")
   int frontendPort;

   @CommandLine.Option(names = {"-m", "--max-content-length"}, description = "The maximum content length", defaultValue = "1000000")
   int maxContentLength;

   @CommandLine.Option(names = {"-c", "--cache"}, description = "Enable proxy cache", defaultValue = "false")
   boolean cache;

   @Override
   public void run() {
      ProxyConfig config = new ProxyConfig();
      config.setHost(frontendHost);
      config.setPort(frontendPort);
      config.setRemotePort(backendPort);
      config.setRemoteHost(backendHost);
      config.setMaxContentLength(maxContentLength);
      if (cache) {
         config.getListeners().addFirst(new ProxyCacheListener());
      }
      Proxy proxy = new Proxy(config);
      try {
         proxy.start();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
