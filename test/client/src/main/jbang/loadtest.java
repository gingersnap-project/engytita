///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.5.0


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.Callable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Command(name = "loadtest", mixinStandardHelpOptions = true, version = "loadtest 0.1",
      description = "loadtest")
class loadtest implements Callable<Integer> {
   static final Pattern WRK_PATTERN = Pattern.compile("^.*Latency\\s+(?<latencyavg>[\\d\\\\.]+.s).*Requests/sec:\\s+(?<reqssec>[\\d\\\\.]+)Transfer/sec:\\s+(?<transfersec>[\\d\\\\.]+.B).*$");

   @CommandLine.Option(names = {"-t", "--threads"}, description = "The number of threads", defaultValue = "1")
   int threadCount;

   @CommandLine.Option(names = {"-c", "--connections"}, description = "The number of connections", defaultValue = "5")
   int connectionCount;

   @CommandLine.Option(names = {"-d", "--duration"}, description = "The duration in seconds", defaultValue = "10")
   int durationSeconds;

   public static void main(String... args) {
      int exitCode = new CommandLine(new loadtest()).execute(args);
      System.exit(exitCode);
   }

   @Override
   public Integer call() throws Exception {
      Map<String, String> urls = new LinkedHashMap<>();
      urls.put("Direct", "http://127.0.0.1:8080/airports/LILY");
      urls.put("Client (direct, no caching)", "http://127.0.0.1:8888/adirectx/LILY");
      urls.put("Client (direct, caffeine)", "http://127.0.0.1:8888/acaffein/LILY");
      urls.put("Client (direct, hot rod)", "http://127.0.0.1:8888/ahotrodx/LILY");
      urls.put("Client (direct, redis)", "http://127.0.0.1:8888/airredis/LILY");
      urls.put("Client (direct, resp)", "http://127.0.0.1:8888/arespalt/LILY");
      urls.put("Proxy (direct)", "http://127.0.0.1:9090/airports/LILY");
      System.out.println("[%header,format=csv]");
      System.out.println("|===");
      System.out.println("Name,Latency Avg,Requests/sec,Transfer/sec");
      urls.entrySet().forEach(u -> {
         String command = String.format("wrk -t%d -c%d -d%ds %s", threadCount, connectionCount, durationSeconds, u.getValue());
         try {
            Process process = Runtime.getRuntime().exec(command);
            try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
               String s = r.lines().collect(Collectors.joining());
               Matcher matcher = WRK_PATTERN.matcher(s);
               if (matcher.matches()) {
                  System.out.printf("%s,%s,%s,%s%n", u.getKey(), matcher.group("latencyavg"), matcher.group("reqssec"), matcher.group("transfersec"));
               } else {
                  System.out.printf("#%s%n", s);
               }
            }
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      });
      System.out.println("|===");
      return 0;
   }
}
