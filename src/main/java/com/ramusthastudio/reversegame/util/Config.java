package com.ramusthastudio.reversegame.util;

import com.ramusthastudio.reversegame.database.Dao;
import com.ramusthastudio.reversegame.database.DaoImpl;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@PropertySource("classpath:application.properties")
public class Config {
  @Bean public DataSource getDataSource() {
    String dbUrl = System.getenv("JDBC_DATABASE_URL");
    String username = System.getenv("JDBC_DATABASE_USERNAME");
    String password = System.getenv("JDBC_DATABASE_PASSWORD");

    DriverManagerDataSource ds = new DriverManagerDataSource();
    ds.setDriverClassName("org.postgresql.Driver");
    ds.setUrl(dbUrl);
    ds.setUsername(username);
    ds.setPassword(password);

    return ds;
  }

  @Bean public Dao getPersonDao() { return new DaoImpl(getDataSource()); }
  @Bean(name = "line.bot.channelSecret")
  public String getChannelSecret() { return System.getenv("line.bot.channelSecret"); }
  @Bean(name = "line.bot.channelToken")
  public String getChannelAccessToken() { return System.getenv("line.bot.channelToken"); }
}
