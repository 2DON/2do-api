package io.github._2don.api.projectmember;

import io.github._2don.api.account.Account;
import io.github._2don.api.project.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ProjectMemberId implements Serializable {

  private Account account;
  private Project project;

}
