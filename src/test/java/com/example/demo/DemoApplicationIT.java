package com.example.demo;

import static com.example.demo.MyContentSecurityPolicyHeaderWriter.CONTENT_SECURITY_POLICY_HEADER;
import static com.example.demo.MyContentSecurityPolicyHeaderWriter.DEFAULT_SRC_SELF_POLICY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoApplicationIT {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void testHome() {
    home();
  }

  @Test
  void testFoo() {
    foo();
  }

  @Test
  void testBr() {
    bar();
  }

  private void home() {
    assertThat(restTemplate.getForEntity("http://localhost:" + port + "/", String.class)
            .getHeaders().get(CONTENT_SECURITY_POLICY_HEADER).contains(DEFAULT_SRC_SELF_POLICY)
    ).isTrue();
  }

  private void foo() {
    assertThat(restTemplate.getForEntity("http://localhost:" + port + "/foo", String.class)
            .getHeaders().get(CONTENT_SECURITY_POLICY_HEADER).contains("FOO")
    ).isTrue();
  }

  private void bar() {
    assertThat(restTemplate.getForEntity("http://localhost:" + port + "/bar", String.class)
            .getHeaders().get(CONTENT_SECURITY_POLICY_HEADER).contains("BAR")
    ).isTrue();
  }

  @Test
  void testParallel() throws Exception {
    final StressTester<Void> stressTestHome = new StressTester<>(Void.class, 2000, 0,
            () -> {
              home();
              return null;
            },
            () -> {
              foo();
              return null;
            },
            () -> {
              bar();
              return null;
            }
    );
    stressTestHome.test();
    stressTestHome.printErrors(System.out);
    assertTrue(stressTestHome.getExceptions().isEmpty());
  }
}
