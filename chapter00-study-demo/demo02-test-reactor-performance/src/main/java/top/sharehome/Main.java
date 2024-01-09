package top.sharehome;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

/**
 * 多种方式多对比,当然这里并不是比较优劣,因为不同方式适用的不同场景不一致
 *
 * @author AntonyCheng
 */

public class Main {

    public static void main(String[] args) throws IOException {

//        // 虚拟线程方式（多线程并发）：5s CPU平均占比20-40%
//        ThreadFactory virtualFactory = Thread.ofVirtual().factory();
//        for (int i = 0; i < 100000; i++) {
//            int taskId = i;
//            virtualFactory.newThread(()->{
//                System.out.println(taskId + "\t" + UUID.randomUUID().toString().replace("-", ""));
//            }).start();
//        }

//        // 平台线程方式（多线程并发）：20s CPU占比最高，100%占比时间最长
//        ThreadFactory factory = Thread.ofPlatform().factory();
//        for (int i = 0; i < 100000; i++) {
//            int taskId = i;
//            factory.newThread(()->{
//                System.out.println(taskId + "\t" + UUID.randomUUID().toString().replace("-", ""));
//            }).start();
//        }

//        // 线程池方式（多线程串行）：545s 主要时间用于切换线程，CPU占比取决于线程池大小
//        for (int i = 0; i < 100000; i++) {
//            int taskId = i;
//            Executors.newFixedThreadPool(12).submit(() -> {
//                System.out.println(taskId + "\t" + UUID.randomUUID().toString().replace("-", ""));
//            });
//        }

        // 响应式方式（多线程并行）：2s 无阻塞,主打的一个非常快！！！
        Flux.range(0, 100000)
                .parallel().runOn(Schedulers.newParallel("flux", 12))
                .subscribe(taskId -> {
                    System.out.println(taskId + "\t" + UUID.randomUUID().toString().replace("-", ""));
                });

        System.in.read();

    }

}
