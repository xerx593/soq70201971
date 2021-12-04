package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class MyContentSecurityDelegate {

  @Getter
  @Setter()
  private String policyDirectives;
}
