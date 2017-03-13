1.注释掉所有数据源的配置，@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
2.在所有依赖数据源的配置上加@ConditionalOnBean(DataSource.class)
3.在所有引用mapper的autowired注解上加required=false
