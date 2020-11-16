package io.github._2don.api.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.github._2don.api.auth.verify.AccountVerification;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {

  @Id
  @GeneratedValue
  @JsonProperty(access = Access.READ_ONLY)
  private Long id;

  @Column(nullable = false, unique = true, length = 45)
  private String email;

  @JsonProperty(access = Access.WRITE_ONLY)
  @Column(nullable = false, columnDefinition = "CHAR(60)")
  private String password;

  @Column(nullable = false, length = 45)
  private String name;

  @JsonIgnore
  @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
  @PrimaryKeyJoinColumn
  private AccountVerification verification;

  @Column(columnDefinition = "TEXT")
  @JsonProperty(access = Access.READ_ONLY)
  private String avatarUrl;

  @Column(nullable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Boolean premium = false;

  @Column(columnDefinition = "TEXT")
  private String options;

  @JsonProperty(access = Access.READ_ONLY)
  private Date deleteRequest;

  @CreationTimestamp
  @Column(nullable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Timestamp updatedAt;

  public Account(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
