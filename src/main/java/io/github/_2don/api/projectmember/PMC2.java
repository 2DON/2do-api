package io.github._2don.api.projectmember;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PMC2 {

  @PostMapping("/projects/float/idk")
  public String handle(@RequestPart(name = "_i") String _i) {
    return String.format("{_i: %s}", _i);
  }

}
