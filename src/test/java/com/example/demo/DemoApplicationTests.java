package com.example.demo;

import static com.example.demo.DemoApplication.MyContentSecurityPolicyHeaderWriter.CONTENT_SECURITY_POLICY_HEADER;
import static com.example.demo.DemoApplication.MyContentSecurityPolicyHeaderWriter.DEFAULT_SRC_SELF_POLICY;
import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testHome() throws Exception {
    this.mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("header reset!")))
            .andExpect(header().string(CONTENT_SECURITY_POLICY_HEADER, DEFAULT_SRC_SELF_POLICY));
  }

  @Test
  public void testFoo() throws Exception {
    this.mockMvc.perform(get("/foo"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Hello from foo!")))
            .andExpect(header().string(CONTENT_SECURITY_POLICY_HEADER, "FOO"));
  }

  @Test
  public void testBar() throws Exception {
    this.mockMvc.perform(get("/bar"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Hello from bar!")))
            .andExpect(header().string(CONTENT_SECURITY_POLICY_HEADER, "BAR"));
  }
}
