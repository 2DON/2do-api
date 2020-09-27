package io.github._2don.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Patterns {
  EMAIL("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

  private Matcher matcher;

  private Patterns(String regex) {
    this.matcher = Pattern.compile(regex).matcher("");
  }

  public boolean matches(String string) {
    return matcher.reset(string).matches();
  }
}
