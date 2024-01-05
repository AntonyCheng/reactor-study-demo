package top.sharehome.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Lambda表达式
 * Lambda表达式是针对函数式接口而言的；
 * 函数式接口：指的是有且仅有一个未被实现的方法的接口，如果有多个方法，需要默认实现。
 *
 * @author AntonyCheng
 */
// 标准的函数式接口
interface StandardInterface {

    // 只有一个未被实现的方法
    int method(int arg);

}

// 带有多个方法的函数式表达式接口
interface DefaultMethodInterface {

    // 默认实现一个方法
    default int defaultMethod(int arg) {
        return method(arg);
    }

    // 只有一个未被实现的方法
    int method(int arg);

}

// 如果不放心接口，可以使用函数式接口检查注解检查一个接口是否是函数式接口
@FunctionalInterface
interface AnnotationInterface {

    // 默认实现一个方法
    default int defaultMethod(int arg) {
        return method(arg);
    }

    // 只有一个未被实现的方法
    int method(int arg);

}

/**
 * Lambda表达式演示类
 *
 * @author AntonyCheng
 */
public class Lambda {

    /**
     * 1、Lambda简化函数式接口实例创建的实例方法
     *
     * @author AntonyCheng
     */
    public static void simplifyInstances() {

        // 1、以前使用接口的方式一般是写一个Impl实现类或者直接使用匿名实现，这里演示匿名实现的效果
        // 匿名实现如下：
        StandardInterface standardInterface = new StandardInterface() {
            @Override
            public int method(int arg) {
                return arg;
            }
        };
        // 使用如下：
        System.out.println("匿名实现的结果：" + standardInterface.method(1));

        // 2、现在可以使用Lambda表达式的写法
        DefaultMethodInterface defaultMethodInterface1 = (x) -> {
            return x * x;
        };
        System.out.println("Lambda表达式的结果：" + defaultMethodInterface1.method(2));
        System.out.println("接口中默认实现的方法结果：" + defaultMethodInterface1.defaultMethod(2));

        // 3、还可以使用Lambda表达式的简写方法
        AnnotationInterface annotationInterface = x -> x * x;
        System.out.println("Lambda表达式的简写结果：" + annotationInterface.method(2));
        System.out.println("接口中默认实现的方法简写结果：" + defaultMethodInterface1.defaultMethod(2));

        // 由上可知Lambda表达式可以简化实例的创建，可能对于业务逻辑来说这种写法并不能体现出优势，如果涉及到大数据或者游戏方面的需要对数据做不断地计算时，函数式接口是一个较为完美的解决方案
        // 下面来简化一下经常用到的一些代码写法，例如给包含英文名称的集合倒叙排序：
        List<String> names = new ArrayList<>() {
            {
                add("Alice");
                add("Bob");
                add("Charles");
                add("David");
            }
        };
        names.sort((o1, o2) -> o2.compareTo(o1));
        // 或者可以使用Comparator类的静态方法，直接返回一个倒叙排序的Lambda函数接口参数
        // names.sort(Comparator.reverseOrder());
        System.out.println("排序后的集合：" + names);

        // 再例如新开一个线程
        System.out.println(Thread.currentThread().getName());
        new Thread(() -> System.out.println(Thread.currentThread().getName())).start();

    }

    /**
     * 2、函数式接口参数返回类型定义
     * （1）、有入参无返回【消费者函数】：Consumer类型，接口变量常调用accept方法；
     * （2）、有入参有返回【多功能函数】：Function类型，接口变量常调用apply方法；
     * （3）、无入参无返回【辅助者函数】：Runnable类型，接口变量常调用run方法；
     * （4）、无入参有返回【提供者函数】：Supplier类型，接口变量常调用get方法；
     *
     * @author AntonyCheng
     */
    public static void paramsReturnType() {
        // 1、消费者函数：打印入参x
        Consumer<String> consumer = x -> System.out.println(x + x);
        consumer.accept("x");

        // 2、多功能函数：返回入参x
        Function<String, String> function = x -> x;
        String x = function.apply("x");
        System.out.println("多功能函数返回值为：" + x);
        // 多功能函数中有一个断言型多功能函数，即有入参，返回Boolean值
        Predicate<Integer> predicate = (i) -> i % 2 == 0;
        // 正向判断，即 2 为偶数返回 ture
        boolean test1 = predicate.test(2);
        System.out.println("正向判断结果：" + test1);
        // 反向判断，即 2 为偶数返回 false
        boolean test2 = predicate.negate().test(2);
        System.out.println("反向判断结果：" + test2);

        // 3、辅助者函数：打印UUID
        Runnable runnable = () -> System.out.println(UUID.randomUUID());
        runnable.run();

        // 4、提供者函数：返回UUID
        Supplier<String> supplier = () -> UUID.randomUUID().toString();
        String uuid = supplier.get();
        System.out.println("提供者函数返回值为：" + uuid);

    }

    /**
     * todo 3、综合示例：
     * （1）使用提供者提供一个字符串
     * （2）使用断言判断这个字符串是不是数字（正则表达式 "-?\\d+(\\.\\d+)?" ）
     * （3）使用多功能函数写一个字符串转数字
     * （4）使用消费者打印该数字是奇数还是偶数
     */
    public static void comprehensiveExample() {

    }

    /**
     * 方法主入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) {
        simplifyInstances();
        paramsReturnType();
        // 使用Lambda表达式的最佳时机：
        // 1、以后调用
    }

}
