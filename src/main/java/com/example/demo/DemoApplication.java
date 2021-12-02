package com.example.demo;

import static com.example.demo.DemoApplication.MyContentSecurityPolicyHeaderWriter.DEFAULT_SRC_SELF_POLICY;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableWebSecurity
public class DemoApplication extends WebSecurityConfigurerAdapter {

  @Getter
  @Setter
  @Component
  static final class MyContentSecurityPolicyHeaderWriter implements HeaderWriter {

    public static final String CONTENT_SECURITY_POLICY_HEADER = "Content-Security-Policy";

    public static final String DEFAULT_SRC_SELF_POLICY = "default-src 'self'";

    @Value("${my.policy.directive:DEFAULT_SRC_SELF_POLICY}")
    private String policyDirectives;

    /**
     * Creates a new instance. Default value: default-src 'self'
     */
    public MyContentSecurityPolicyHeaderWriter() {
      setPolicyDirectives(DEFAULT_SRC_SELF_POLICY);
    }

    /**
     * Creates a new instance
     *
     * @param policyDirectives maps to {@link #setPolicyDirectives(String)}
     * @throws IllegalArgumentException if policyDirectives is null or empty
     */
    public MyContentSecurityPolicyHeaderWriter(String policyDirectives) {
      setPolicyDirectives(policyDirectives);
    }

    /**
     * @see org.springframework.security.web.header.HeaderWriter#writeHeaders(jakarta.servlet.http.HttpServletRequest,
     * jakarta.servlet.http.HttpServletResponse)
     */
    @Override
    public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
      if (!response.containsHeader(CONTENT_SECURITY_POLICY_HEADER)) {
        response.setHeader(CONTENT_SECURITY_POLICY_HEADER, policyDirectives);
      }
    }

    /**
     * Sets the security policy directive(s) to be used in the response header.
     *
     * @param policyDirectives the security policy directive(s)
     * @throws IllegalArgumentException if policyDirectives is null or empty
     */
    public void setPolicyDirectives(String policyDirectives) {
      Assert.hasLength(policyDirectives, "policyDirectives cannot be null or empty");
      this.policyDirectives = policyDirectives;
    }

    @Override
    public String toString() {
      return getClass().getName() + " [policyDirectives=" + policyDirectives + "]";
    }

  }

  @Autowired
  private MyContentSecurityPolicyHeaderWriter myHeadersWriter;

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // ... lots more config here...
    http.headers()
            .addHeaderWriter(myHeadersWriter);
  }

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

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }
}
