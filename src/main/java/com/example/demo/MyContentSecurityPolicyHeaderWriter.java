package com.example.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.header.HeaderWriter;
// abstract ;(;(;(
public abstract class MyContentSecurityPolicyHeaderWriter implements HeaderWriter {

  public static final String CONTENT_SECURITY_POLICY_HEADER = "Content-Security-Policy";
  public static final String DEFAULT_SRC_SELF_POLICY = "default-src 'self'";

  @Override
  public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
    if (!response.containsHeader(CONTENT_SECURITY_POLICY_HEADER)) {
      response.setHeader(CONTENT_SECURITY_POLICY_HEADER, policyDelegate().getPolicyDirectives());
    }
  }

  // will be injected by spring, has "state"! :)
  protected abstract MyContentSecurityDelegate policyDelegate();
}
