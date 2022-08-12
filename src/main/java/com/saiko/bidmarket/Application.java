package com.saiko.bidmarket;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.saiko.bidmarket.common.config.JwtConfig;

@EnableConfigurationProperties(JwtConfig.class)
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @PostConstruct
  public void started(){
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
  }
}
