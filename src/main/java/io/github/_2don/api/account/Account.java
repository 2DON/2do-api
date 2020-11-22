package io.github._2don.api.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false, unique = true, length = 45)
  private String email;

  @JsonIgnore
  @Column(nullable = false, columnDefinition = "CHAR(60)")
  private String password;

  @Column(nullable = false, length = 45)
  private String name;

  @JsonIgnore
  private Timestamp verificationSentAt;

  @Column(columnDefinition = "TEXT")
  private String avatarUrl;

  @Column(nullable = false)
  private Boolean premium = false;

  @Column(columnDefinition = "TEXT")
  private String options;

  private Date deleteRequest;

  @CreationTimestamp
  @Column(nullable = false)
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private Timestamp updatedAt;

  public Account(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public boolean isVerified() {
    return this.verificationSentAt == null;
  }

}
