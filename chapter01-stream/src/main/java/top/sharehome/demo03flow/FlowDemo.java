package top.sharehome.demo03flow;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * Flow示例代码
 * 这里演示的是Reactive Streams规范的示例代码，它存在于Java9+版本JUC包下Flow类中。
 * 在这个规范中有四个组件：
 * 1、发布者（必要）
 * 2、处理器（不必要，它本身就是一个发布者和订阅者的融合体）
 * 3、订阅者（必要）
 * 4、订阅关系（必要）
 * 他们可以按照下面这个顺序进行工作流：
 * 发布者 ==发布消息==> 缓冲区 ==取出消息==> 处理器 ==处理完后的消息==> 缓冲区 ==取出消息==> 订阅者
 * 注意：以上的缓冲区为同一个缓冲区，该缓冲区由JMV自身维护，同时还维护着一个线程池；处理器和订阅者可以有多个，这样就预示着有多个订阅关系；
 *
 * @author AntonyCheng
 */

public class FlowDemo {

    /**
     * 定义处理器类
     * 由于处理器既是发布者，也是订阅者，Flow.Processor接口中就包含了Flow.Publisher和Flow.Subscriber中包含的方法；
     * 所以继承SubmissionPublisher就是为了方便不再去考虑实现Flow.Publisher中的方法，以免自己写submit函数。
     *
     * @author AntonyCheng
     */
    private static class Processor extends SubmissionPublisher<String> implements Flow.Processor<String, String> {

        // 将订阅关系转变为变量，方便能够拿出发布者发布在缓存中的数据
        private Flow.Subscription subscription;

        private String processorCode;

        Processor() {

        }

        Processor(String processorCode) {
            this.processorCode = processorCode;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            System.out.println(Thread.currentThread() + " 处理器" + processorCode + "开始订阅：" + subscription);
            // 向缓存请求一条数据以便于激活处理器
            subscription.request(1);
            this.subscription = subscription;
        }

        @Override
        public void onNext(String item) {
            System.out.println(Thread.currentThread() + " 处理器" + processorCode + "开始处理：" + item);
            item = "p" + item;
            submit(item);
            // 每次操作完之后向缓存请求一条数据
            subscription.request(1);
        }

        // 在接收到错误信号时
        @Override
        public void onError(Throwable throwable) {
            System.out.println(Thread.currentThread() + " 处理器" + processorCode + "接收到错误信号：" + throwable);
        }

        // 在接收完成时
        @Override
        public void onComplete() {
            System.out.println(Thread.currentThread() + " 处理器" + processorCode + "接收完成信息");
        }
    }

    /**
     * 方法入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) throws InterruptedException {

        // 1、定义一个发布者
        SubmissionPublisher<String> publisher = new SubmissionPublisher<String>();

        // 2、定义两个处理器，在每个元素前面加一个"p"
        Processor processor1 = new Processor("1");
        Processor processor2 = new Processor("2");

        // 3、定义两个订阅者：订阅数据
        Flow.Subscriber<String> subscriber1 = new Flow.Subscriber<String>() {

            // 将订阅关系转变为变量，方便能够拿出发布者发布在缓存中的数据
            private Flow.Subscription subscription;

            // 在订阅开始时要执行的回调
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println(Thread.currentThread() + " 订阅者1号开始订阅：" + subscription);
                // 向缓存请求一条数据以便于激活订阅者
                subscription.request(1);
                this.subscription = subscription;
            }

            // 在下一个元素到达时，即接收到数据时
            @Override
            public void onNext(String item) {
                System.out.println(Thread.currentThread() + " 订阅者1号接收到的消息：" + item);
                if ("ppp-7".equals(item)) {
                    // 模拟取消订阅，当拿到"pp-7"时取消订阅
                    subscription.cancel();
                } else {
                    // 每次操作完之后向缓存请求一条数据
                    subscription.request(1);
                }
            }

            // 在接收到错误信号时
            @Override
            public void onError(Throwable throwable) {
                System.out.println(Thread.currentThread() + " 订阅者1号接收到错误信号：" + throwable);
            }

            // 在接收完成时
            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread() + " 订阅者1号接收完成信息");
            }
        };
        Flow.Subscriber<String> subscriber2 = new Flow.Subscriber<String>() {

            // 将订阅关系转变为变量，方便能够拿出发布者发布在缓存中的数据
            private Flow.Subscription subscription;

            // 在订阅开始时要执行的回调
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println(Thread.currentThread() + " 订阅者2号开始订阅：" + subscription);
                // 向缓存请求一条数据以便于激活订阅者
                subscription.request(1);
                this.subscription = subscription;
            }

            // 在下一个元素到达时，即接收到数据时
            @Override
            public void onNext(String item) {
                System.out.println(Thread.currentThread() + " 订阅者2号接收到的消息：" + item);
                // 每次操作完之后向缓存请求一条数据
                subscription.request(1);
            }

            // 在接收到错误信号时
            @Override
            public void onError(Throwable throwable) {
                System.out.println(Thread.currentThread() + " 订阅者2号接收到错误信号：" + throwable);
            }

            // 在接收完成时
            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread() + " 订阅者2号接收完成信息");
            }
        };

        // 4、绑定发布者、处理器和订阅者（责任链）
        publisher.subscribe(processor1);     // 由发布者发布信息给处理器1
        processor1.subscribe(processor2);    // 由发布者发布信息给处理器2
        processor2.subscribe(subscriber1);   // 由处理器2发布信息给订阅者1
        processor2.subscribe(subscriber2);   // 由处理器2发布信息给订阅者2

        // 5、确定绑定上之后开始发送数据
        for (int i = 0; i < 10; i++) {
            if (i < 9) {
                // 隔一秒发一条，以免发太快缓存中来不及读取消息就抛出了异常
                Thread.sleep(1000);
                // 发布者发布十条数据到Buffer区
                publisher.submit("p-" + i);
            } else {
                // 模拟一个异常抛出，中断发布
                publisher.closeExceptionally(new RuntimeException("发布者太累了，不想干了..."));
            }
        }

        // 6、关闭发布者通道，能够激活订阅者的onComplete回调函数，允许重复调用，但是对关闭的发布者调用close方法无效
        publisher.close();

        // 绑定之后发布者有数据，订阅者就会拿到，但是在测试时需要保证主线程不被停掉
        Thread.sleep(20000);

    }

}
