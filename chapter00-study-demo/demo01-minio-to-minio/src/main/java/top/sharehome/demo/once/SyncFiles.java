package top.sharehome.demo.once;

import cn.hutool.core.io.IoUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import top.sharehome.demo.mapper.SpecieDataFileMapper;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.stream.Stream;

/**
 * 项目启动就同步文件
 *
 * @author AntonyCheng
 */
@Component
@Slf4j
public class SyncFiles implements CommandLineRunner {

    @Resource
    private SpecieDataFileMapper specieDataFileMapper;

    @Override
    public void run(String... args) {
        Stream<String> pathStream = specieDataFileMapper.getAllPath().stream();
        Flux.fromStream(pathStream)
                // 开启并行且运行在名为"sync"的36线程执行器上
                .parallel().runOn(Schedulers.newParallel("sync", 36))
                .subscribe(path -> {
                    try {
                        minioToMinio(path);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    log.info(path);
                }, throwable -> {
                    log.error("error:" + throwable);
                });
    }

    /**
     * 假设path是URI，形式为：/桶名称/文件路径.../文件本体
     * @param path
     * @throws Exception
     */
    private void minioToMinio(String path) throws Exception {
        String bucketName = "demo_bucket";
        String objectName = "object_name";
        //源文件minIO地址
        final String MINIO_URL = "http://xxx.xxx.xxx.xxx:9000";
        //源文件minIOAccessKey
        final String MINIO_ACCESS_KEY = "minio";
        //源文件minIOSecretKey
        final String MINIO_SECRET_KEY = "minio123";

        //目标文件minIO地址
        final String SJPT_MINIO_URL = "http://xxx.xxx.xxx.xxx:9000";
        //目标文件minIOAccessKey
        final String SJPT_MINIO_ACCESS_KEY = "minio";
        //目标文件minIOSecretKey
        final String SJPT_MINIO_SECRET_KEY = "minio123";
        //源文件路径
        if (StringUtils.isNotBlank(path)) {
            //设置桶名称
            int startIndex = path.indexOf("/") + 1; // 起始索引是第一个/后面
            int endIndex = path.indexOf("/", startIndex); // 结束索引是第二个/
            bucketName = path.substring(startIndex, endIndex);
            //设置源文件名称
            int secondSlashIndex = path.indexOf("/", path.indexOf("/") + 1);
            String resultPath = path.substring(secondSlashIndex + 1);
            objectName = resultPath;
        }
        //构造源文件客户端（IP地址、用户名、密码）
        MinioClient srcClient = MinioClient.builder()
                .endpoint(MINIO_URL)
                .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
                .build();
        //构造目标文件客户端（IP地址、用户名、密码）
        MinioClient destClient = MinioClient.builder()
                .endpoint(SJPT_MINIO_URL)
                .credentials(SJPT_MINIO_ACCESS_KEY, SJPT_MINIO_SECRET_KEY)
                .build();
        //构造源文件参数
        GetObjectArgs srcArgs = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        //获取流数据
        InputStream stream = srcClient.getObject(srcArgs);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        IoUtil.copy(stream, bs);
        stream = new ByteArrayInputStream(bs.toByteArray());
        //构造发送数据参数
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(stream, stream.available(), -1)
                //.contentType("application/octet-stream")
                .build();
        //发送数据
        destClient.putObject(putObjectArgs);
        stream.close();
    }

}