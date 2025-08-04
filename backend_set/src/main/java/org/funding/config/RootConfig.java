package org.funding.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource({"classpath:/application.properties"})
@MapperScan({
        "org.funding.user.dao",
        "org.funding.emailAuth.dao",
        "org.funding.openAi.dao",
        "org.funding.badge.dao",
        "org.funding.financialProduct.dao",
        "org.funding.fund.dao",
        "org.funding.votes.dao",
        "org.funding.retryVotes.dao",
        "org.funding.chatting.dao",
        "org.funding.comment.dao",
        "org.funding.project.dao",
        "org.funding.category.dao",
        "org.funding.keyword.dao",
        "org.funding.userKeyword.dao",
        "org.funding.projectKeyword.dao",
        "org.funding.payment.dao",
        "org.funding.userDonation.dao",
        "org.funding.userChallenge.dao",
        "org.funding.challengeLog.dao",
        "org.funding.S3.dao",
})
public class RootConfig {
  @Value("${jdbc.driver}")
  private String driver;
  @Value("${jdbc.url}")
  private String url;
  @Value("${jdbc.username}")
  private String username;
  @Value("${jdbc.password}")
  private String password;
  @Autowired
  private ApplicationContext applicationContext;


  @Bean
  public DataSource dataSource() {
    // HikariCP 설정 객체 생성
    HikariConfig config = new HikariConfig();

    // 데이터베이스 연결 정보 설정
    config.setDriverClassName(driver);          // JDBC 드라이버 클래스
    config.setJdbcUrl(url);                    // 데이터베이스 URL
    config.setUsername(username);              // 사용자명
    config.setPassword(password);              // 비밀번호

    // 커넥션 풀 추가 설정 (선택사항)
    config.setMaximumPoolSize(10);             // 최대 커넥션 수
    config.setMinimumIdle(5);                  // 최소 유지 커넥션 수
    config.setConnectionTimeout(30000);       // 연결 타임아웃 (30초)
    config.setIdleTimeout(600000);            // 유휴 타임아웃 (10분)

    // HikariDataSource 생성 및 반환
    HikariDataSource dataSource = new HikariDataSource(config);
    return dataSource;
  }

  @Bean
  public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
    SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();

    // MyBatis 설정 파일 위치 지정
    sqlSessionFactory.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml"));

    // 데이터베이스 연결 설정
    sqlSessionFactory.setDataSource(dataSource);

    return sqlSessionFactory.getObject();
  }

  @Bean
  public DataSourceTransactionManager transactionManager(DataSource dataSource) {
    DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
    return manager;
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

}
