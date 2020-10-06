package io.github._2don.api.models;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.GenerationType;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.JoinColumn;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Team {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty(access = Access.READ_ONLY)
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String avatarURL;

  @NotNull
  @Size(min = 1, max = 160)
  @Column(nullable = false, length = 160)
  private String name;

  @CreationTimestamp
  @JsonProperty(access = Access.READ_ONLY)
  @Column(nullable = false)
  private Timestamp createdAt;

  @UpdateTimestamp
  @JsonProperty(access = Access.READ_ONLY)
  @Column(nullable = false)
  private Timestamp updatedAt;

  @ManyToOne
  @JsonProperty(access = Access.READ_ONLY)
  @JoinColumn(referencedColumnName = "id", nullable = false)
  private Account createdBy;

  @ManyToOne
  @JsonProperty(access = Access.READ_ONLY)
  @JoinColumn(referencedColumnName = "id", nullable = false)
  private Account updatedBy;
}
