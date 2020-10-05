package io.github._2don.api.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.sql.Date;

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

  @Column(nullable = false, unique = true, length = 50)
  private String email;

  @JsonProperty(access = Access.WRITE_ONLY)
  private String password;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  @JsonProperty(access = Access.READ_ONLY)
  private String avatarUrl;

  @Column(nullable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Boolean premium = false;

  private String options;

  @JsonProperty(access = Access.READ_ONLY)
  private Date deleteRequest;

  @JsonProperty(access = Access.READ_ONLY)
  private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

  public Account(String email, String password) {
    this.email = email;
    this.password = password;
  }

}
