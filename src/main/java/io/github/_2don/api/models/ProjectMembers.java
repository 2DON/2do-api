package io.github._2don.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@IdClass(ProjectMembersId.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectMembers {

  // TODO JSON config

  // FIXME field is NOT NULL, but shout not be
  @Id
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private Account account;

  @Id
  @ManyToOne
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JoinColumn(referencedColumnName = "id", nullable = false)
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private Project project;

  // FIXME field is NOT NULL, but shout not be
  @Id
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private Team team;

  // TODO enum ProjectMembersPermissions
  @NotNull
  @Column(nullable = false)
  private Byte permissions;

  @CreationTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp updatedAt;

}
