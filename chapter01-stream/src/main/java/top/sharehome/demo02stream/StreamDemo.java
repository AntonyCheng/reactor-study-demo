package top.sharehome.demo02stream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Stream示例代码
 *
 * @author AntonyCheng
 */

public class StreamDemo {

    /**
     * 方法入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) {

        // 首先来一道例题：随机生成10个整数存放在集合中，然后从集合这挑出最大的那个偶数，如果没有即为0
        // 平时用for循环的方式如下：
        List<Integer> nums = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            int num = new Random().nextInt();
            nums.add(num);
        }
        System.out.println("生成的集合如下：" + nums);
        Integer max1 = nums.get(0);
        for (Integer num : nums) {
            if (num % 2 == 0) {
                if (max1 % 2 != 0) {
                    max1 = num;
                } else {
                    max1 = num > max1 ? num : max1;
                }
            }
        }
        if (max1 % 2 != 0) {
            System.out.println("随机数中没有偶数");
        } else {
            System.out.println("最大的偶数是：" + max1);
        }
        // 用Stream流的方式如下：
        Stream<Integer> numStream1 = nums.stream();
        System.out.println(
                numStream1.filter(num -> num % 2 == 0)
                        .max(Integer::compareTo)
                        .map(integer -> "最大的偶数是：" + integer)
                        .orElse("随机数中没有偶数")
        );
        // 从上面这个例题来看，Stream这样的响应式编程（基于事件机制的回调函数，只给出算子指令【filter、max等】就能够得到相应地答案）能够极大的简化代码逻辑，具有以下特点：
        //（1）Stream的运算分为三个步骤：封装流（由迭代器转换为Stream类型）-->定义流式操作（过滤、转换或者筛选等）-->获取最终结果（最大值、最小值或者收集为集合等）
        //     这三个步骤分别流的三大部分：
        //     1）一个数据流：创建流的常用方法有Collection.stream()、Stream.of()、Stream.builder()、Stream.concat();
        Stream<Integer> stream1 = new ArrayList<Integer>().stream();
        Stream<Integer> stream2 = Stream.of(1, 2, 3);
        Stream<Integer> stream3 = Stream.<Integer>builder().add(4).add(5).add(6).build();
        Stream<Integer> stream4 = Stream.concat(stream2, stream3);
        //     2）多个中间操作：
        //       常见的算子 filter逐个过滤（流中有满足条件的元素就过滤）、peek观察流中元素、
        //                map一对一映射、flatMap一对多映射（散列、展开或增加维度）、
        //                distinct去重、sorted排序、limit截取流、parallel使流并发、
        //                takeWhile整体过滤（整个流中的元素全部满足条件才过滤）；
        List<String> list = List.of("11", "11", "22", "22", "33", "33", "44", "44", "55", "55");
        list.stream()
                // 逐个过滤后剩下偶数字符串
                .filter(num -> Integer.parseInt(num) % 2 == 0)
                // 观察流中元素（peek和foreach的不同点在于peek属于中间操作算子，foreach属于终止操作算子）
                .peek(System.out::println)
                // 将流中的偶数字符串一对一映射成其加一后的奇数字符串
                .map(num -> String.valueOf(Integer.parseInt(num) + 1))
                // 将流中所有字符串打散成字符流
                .flatMap(num -> {
                    char[] charArray = num.toCharArray();
                    return Stream.of(charArray[0], charArray[1]);
                })
                // 将流中的字符去重
                .distinct()
                // 将流中的字符按照ASCII码值进行倒序排序
                .sorted(Comparator.reverseOrder())
                // 截取流中前三个元素
                .limit(3)
                // 将流转变为并发流（后续对于该算子有详解）
                .parallel()
                // 当并发流中所有字符的ASCII码值大于'0'时才让流通过
                .takeWhile(character -> {
                    // 验证并发，打印并发线程名称和元素内容
                    System.out.println(character + "   " + Thread.currentThread().getName());
                    return character > '0';
                })
                // 将并发流中的数据全部打印出来
                .forEach(System.out::println);
        //     3）一个终止操作
        //       常见的算子有 forEach遍历、toArray转数组、collect收集成集合、count计数、anyMatch匹配、findFirst找第一个、findAny找任何一个。
        //（2）流的操作尽量需要是无状态的，因为流的并发操作可能会产生线程不安全的情况，如果要使用一旦产生状态，需要开发者自行加锁消除线程不安全的隐患
        // 首先创建一个外部集合；
        ArrayList<Integer> outerList = new ArrayList<>();
        // 然后对流进行操作；
        List.of(1, 2, 3, 4, 5, 6).stream()
                // 转变为并发流；
                .parallel()
                // 将奇数过滤掉，让流中只剩下偶数，同时将过滤掉的奇数存入外部集合中；
                .filter(num -> {
                    // 此时将流中的元素添加入外部集合中就直接引入了外部状态，就会产生线程不安全的情况，有两种解决办法：
                    // Ⅰ、对流中产生线程安全问题的算子操作进行加锁处理synchronized，下面的代码中进行了演示；
                    // Ⅱ、将外部集合在流外部转换成线程安全的集合Collections.synchronizedList(outerList)，其内部也是加锁处理，在此不再演示；
                    synchronized (Object.class) {
                        if (num % 2 != 0) {
                            outerList.add(num); // 线程不安全操作
                            return false;
                        }
                        return true;
                    }
                })
                .forEach(System.out::println);
        // 所以为了避免线程安全问题，需要把流中的所有操作和数据设计成无状态的；
        //（3）流是Lazy（懒加载）的，如果一个流不存在终止操作，那么这个流中的所有中间操作将不会被执行，例如以下示例就不会被执行；
        Stream<Integer> numStream2 = nums.stream();
        numStream2.filter(num -> {
            System.out.println("验证懒加载");
            return num % 2 == 0;
        }).filter(num -> num % 2 == 0);
    }

}
