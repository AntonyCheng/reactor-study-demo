package top.sharehome.demo02apis;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 阻塞式方法示例代码
 * 其实这里就3个Api：block、blockLast、blockFirst。
 * 并不是所有业务方法都需需要在所有时候均处于响应式状态，所以在Reactor中也有将响应式状态转换为阻塞式的方法；
 * 注意！！！但是如果使用了类似于WebFlux这样的框架，在多线程环境下将响应式强制转换为阻塞式就非常容易出问题，所以这里单独拿出来讨论。
 *
 * @author AntonyCheng
 */

public class BlockApisDemo {

    /**
     * 方法入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) {
        // 创建1~10数据流
        List<Integer> blockList = Flux.range(1, 10)
                // 使数据流中数据增加一倍
                .map(data -> data + data)
                // 然后收集成集合数据流
                .collectList()
                // 突然有一瞬间想得到收集而来的ArrayList类型
                .block();
        System.out.println("block list... " + blockList);

        // 创建1~10数据流
        Integer firstData = Flux.range(1, 10)
                // 使数据流中数据增加一倍
                .map(data -> data + data)
                // 突然有一瞬间想得到数据流第一个Integer类型元素
                .blockFirst();
        System.out.println("first data... "+firstData);

        // 创建1~10数据流
        Integer laseData = Flux.range(1, 10)
                // 使数据流中数据增加一倍
                .map(data -> data + data)
                // 突然有一瞬间想得到数据流最后一个Integer类型元素
                .blockLast();
        System.out.println("last data... "+laseData);
    }

}
