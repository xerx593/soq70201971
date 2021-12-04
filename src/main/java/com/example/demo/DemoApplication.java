package com.example.demo;

import static com.example.demo.MyContentSecurityPolicyHeaderWriter.DEFAULT_SRC_SELF_POLICY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@SpringBootApplication
public class DemoApplication {

  @Configuration
  static class FreakyConfig {

    @Value("${my.policy.directive:DEFAULT_SRC_SELF_POLICY}")
    private String policy;

    @Bean
    @RequestScope
    public MyContentSecurityDelegate delegate() {
      return MyContentSecurityDelegate.of(policy);
    }

    @Bean// abstract class!, singleton (spring-sec), refers to delegate with method injection in his "writeHeaders" method
    public MyContentSecurityPolicyHeaderWriter myWriter() {
      return new MyContentSecurityPolicyHeaderWriter() {
        @Override
        protected MyContentSecurityDelegate policyDelegate() {
          return delegate();
        }
      };
    }
  }

  @Configuration
  @EnableWebSecurity
  static class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MyContentSecurityPolicyHeaderWriter myHeadersWriter;

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
    private MyContentSecurityDelegate myRequestScopedDelegate;

    @GetMapping("/")
    public String home() {
      myRequestScopedDelegate.setPolicyDirectives(DEFAULT_SRC_SELF_POLICY);
      return "header reset!";
    }

    @GetMapping("/foo")
    public String foo() {
      myRequestScopedDelegate.setPolicyDirectives("FOO");
      return "Hello from foo!";
    }

    @GetMapping("/bar")
    public String bar() {
      myRequestScopedDelegate.setPolicyDirectives("BAR");
      return "Hello from bar!";
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }
}
