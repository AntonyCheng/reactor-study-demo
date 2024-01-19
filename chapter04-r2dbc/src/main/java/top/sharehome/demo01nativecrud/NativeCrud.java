package top.sharehome.demo01nativecrud;

import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Mono;
import top.sharehome.demo01nativecrud.entity.Author;

import java.io.IOException;

/**
 * todo: 补充增删查改以及join查询的示例
 * 使用R2DBC进行简单的增删查改操作
 * 其实R2DBC和JDBC插入效果相同，编码逻辑和方案有所区别，在进行测试之前无比将sql包下的数据库文件进行导入
 *
 * @author AntonyCheng
 */
public class NativeCrud {

    // MySQL配置静态值
    private static final MySqlConnectionConfiguration MY_SQL_CONNECTION_CONFIGURATION = MySqlConnectionConfiguration.builder()
            .host("localhost")
            .port(3306)
            .username("root")
            .password("123456")
            .database("test")
            .build();

    // 配置连接工厂静态值
    private static final ConnectionFactory CONNECTION_FACTORY = MySqlConnectionFactory.from(MY_SQL_CONNECTION_CONFIGURATION);

    // 封装连接Mono对象
    private static final Mono<Connection> CONNECTION_MONO = Mono.from(CONNECTION_FACTORY.create());

    /**
     * 使用原生方法进行增加数据
     *
     * @param author 增加的数据
     */
    private static void insertById(Author author) {

        CONNECTION_MONO.subscribe(connection -> {
            Mono.from(connection.createStatement("insert into `t_author` (id,name) values (?id,?name)")
                            .bind("id", author.getId())
                            .bind("name", author.getName())
                            .execute())
                    .subscribe(result -> {
                        result
                                .map(readable -> readable.get(0) + "\t" + readable.get(1))
                                .subscribe(new BaseSubscriber<String>() {
                                    @Override
                                    protected void hookOnError(Throwable throwable) {
                                        System.out.println(throwable);
                                    }

                                    @Override
                                    protected void hookOnComplete() {
                                        System.out.println("插入成功：" + author);
                                    }
                                });

                    });
        });

    }

    /**
     * 使用原生方法进行删除数据
     *
     * @param id 删除数据的ID
     */
    private static void deleteById(Long id) {
        CONNECTION_MONO.subscribe(connection -> {
            Mono.from(connection.createStatement("delete from `t_author` where id = ?id")
                            .bind("id", id)
                            .execute())
                    .subscribe(result -> result
                            .map(readable -> readable.get(0) + "\t" + readable.get(1))
                            .subscribe(new BaseSubscriber<String>() {
                                @Override
                                protected void hookOnError(Throwable throwable) {
                                    System.out.println(throwable);
                                }

                                @Override
                                protected void hookOnComplete() {
                                    System.out.println("删除成功：" + id);
                                }
                            }));
        });
    }

    /**
     * 使用原生方法进行查询数据
     *
     * @param id 查询数据的ID
     */
    private static void selectById(Long id) {
        CONNECTION_MONO.subscribe(connection -> {
            Mono.from(connection.createStatement("select id,name from t_author where id = ?id")
                            .bind("id", id)
                            .execute())
                    .subscribe(result -> result.map(readable -> new Author(readable.get("id", Long.class), readable.get("name", String.class)))
                            .subscribe(new BaseSubscriber<Author>() {
                                @Override
                                protected void hookOnNext(Author value) {
                                    System.out.println("查到数据：" + value);
                                }

                                @Override
                                protected void hookOnError(Throwable throwable) {
                                    System.out.println(throwable);
                                }
                            }));
        });
    }

    /**
     * 使用原生方法进行修改数据
     *
     * @param author 修改后的数据
     */
    private static void updateById(Author author) {
        CONNECTION_MONO.subscribe(connection -> {
            Mono.from(connection.createStatement("update t_author set name = ?name where id = ?id")
                            .bind("id", author.getId())
                            .bind("name", author.getName())
                            .execute())
                    .subscribe(result -> result.map(readable -> readable.get(0) + "\t" + readable.get(1))
                            .subscribe(new BaseSubscriber<String>() {
                                @Override
                                protected void hookOnComplete() {
                                    System.out.println("修改成功：" + author);
                                }

                                @Override
                                protected void hookOnError(Throwable throwable) {
                                    System.out.println(throwable);
                                }
                            }));
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // 增
        Author author1 = new Author().setId(1L).setName("AntonyCheng");
        Author author2 = new Author().setId(2L).setName("AntonyCheng");
        Author author3 = new Author().setId(2L).setName("AntonyCheng");
        insertById(author1);
        insertById(author2);
        insertById(author3);

        // 由于全异步，此时需要线程睡一下，看看效果
        Thread.sleep(1000L);

        // 删
        deleteById(1L);

        // 由于全异步，此时需要线程睡一下，看看效果
        Thread.sleep(1000L);

        // 查
        selectById(2L);

        // 由于全异步，此时需要线程睡一下，看看效果
        Thread.sleep(1000L);

        // 改
        Author author = new Author(2L, "new AntonyCheng");
        updateById(author);

        System.in.read();
    }

}
