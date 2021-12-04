package com.example.demo;

import static com.example.demo.MyContentSecurityPolicyHeaderWriter.CONTENT_SECURITY_POLICY_HEADER;
import static com.example.demo.MyContentSecurityPolicyHeaderWriter.DEFAULT_SRC_SELF_POLICY;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testHome() throws Exception {
    home();
  }

  @Test
  void testFoo() throws Exception {
    foo();
  }

  @Test
  void testBar() throws Exception {
    bar();
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
            });
    stressTestHome.test();
    stressTestHome.printErrors(System.out);
    assertTrue(stressTestHome.getExceptions().isEmpty());
  }

  private void home() throws Exception {
    this.mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("header reset!")))
            .andExpect(header().string(CONTENT_SECURITY_POLICY_HEADER, DEFAULT_SRC_SELF_POLICY));
  }

  private void foo() throws Exception {
    this.mockMvc.perform(get("/foo"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Hello from foo!")))
            .andExpect(header().string(CONTENT_SECURITY_POLICY_HEADER, "FOO"));
  }

  private void bar() throws Exception {
    this.mockMvc.perform(get("/bar"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Hello from bar!")))
            .andExpect(header().string(CONTENT_SECURITY_POLICY_HEADER, "BAR"));
  }
}
