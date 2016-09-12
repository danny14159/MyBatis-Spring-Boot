package tk.mybatis.springboot.conf;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 数据库配置
 * Created by Jim.Xu on 15/3/19.</h6>
 */
@Configuration
@EnableConfigurationProperties({DruidDataSourceProperties.class})
public class DataSourceConfiguration {

    /**
     * Druid DataSource
     *
     * @return dataSource Bean
     */
    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
    @Autowired
    public DataSource dataSource(DruidDataSourceProperties properties) throws Exception {
        return DruidDataSourceFactory.createDataSource(properties.toProperties());
    }
}
