package top.sharehome.demo02apis;

import reactor.core.publisher.Flux;

/**
 * 常规方法示例代码
 * 在这里会介绍一些Reactor中常用到的一些方法：
 * filter、flatMap、concatMap、flatMapMany、transform、defaultIfEmpty、switchIfEmpty、concat、
 * concatWith、merge、mergeWith、mergeSequential、zip、zipWith
 *
 * @author AntonyCheng
 */
public class ApisDemo {

    /**
     * 方法入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) {

        // filter 过滤API
        Flux.range(1,10)
                // 挑选出偶数
                .filter(data->data%2==0)
                .log()
                .subscribe();

        System.out.println("Filter... Done");

        // flatMap 打散/增维API
        Flux.just("Emma Johnson","Benjamin Miller","Sophia Davis")
                // 将姓名分隔开之后再放入数据流中
                .flatMap(data->{
                    String[] s = data.split(" ");
                    return Flux.fromArray(s);
                })
                .log()
                .subscribe();

        System.out.println("FlatMap... Done");




    }
}
