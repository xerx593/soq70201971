package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

  public static final String DEFAULT_SRC_SELF_POLICY = "default-src 'self'";

  @Bean
  public ContentSecurityPolicyHeaderWriter myWriter(
          @Value("${#my.policy.directive:DEFAULT_SRC_SELF_POLICY}") String initalDirectives
  ) {
    return new ContentSecurityPolicyHeaderWriter(initalDirectives);
  }

  @Configuration
  @EnableWebSecurity
  static class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ContentSecurityPolicyHeaderWriter myHeadersWriter;

    @Override
    public void configure(HttpSecurity http) throws Exception {
      // ... lots more config here...
      http.headers()
              .addHeaderWriter(myHeadersWriter);
    }
  }

  @RestController
  static class Controller {

    @Autowired
    private ContentSecurityPolicyHeaderWriter myHeadersWriter;

    @GetMapping("/")
    public String home() {
      myHeadersWriter.setPolicyDirectives(DEFAULT_SRC_SELF_POLICY);
      return "header reset!";
    }

    @GetMapping("/foo")
    public String foo() {
      myHeadersWriter.setPolicyDirectives("FOO");
      return "Hello from foo!";
    }

    @GetMapping("/bar")
    public String bar() {
      myHeadersWriter.setPolicyDirectives("BAR");
      return "Hello from bar!";
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }
}
