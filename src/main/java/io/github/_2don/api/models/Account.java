package io.github._2don.api.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {

  @Id
  @GeneratedValue
  @JsonProperty(access = Access.READ_ONLY)
  private Long id;

  @NotNull
  @Size(min = 4, max = 20)
  @Column(nullable = false, unique = true, length = 20)
  private String username;

  @Size(min = 8, max = 60)
  @Column(nullable = false, length = 60)
  @JsonProperty(access = Access.WRITE_ONLY)
  private String password;

  public Account(String username, String password) {
    this.username = username;
    this.password = password;
  }

}
