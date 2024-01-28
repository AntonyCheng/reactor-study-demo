# 响应式编程Reactor学习示例仓库

**软件版本如下**：
* Java 21
* reactor-bom 2023.0.2 (reactor 3.6.2)
* Spring Boot 3.1.7
* r2dbc-mysql 1.0.5
* lombok 1.18.30

**历史存留问题**：
1. Spring Security整合不全面，但是考虑到其过强的侵入性，暂定如此
2. 理论上R2DBC-mysql应该将时间映射成LocalDateTime，但是在查询时会被映射成ZonedDateTime，目前原因不祥，可以使用ZonedDateTime.toLocalDateTime方法进行转换