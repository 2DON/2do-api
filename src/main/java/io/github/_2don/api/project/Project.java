package io.github._2don.api.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github._2don.api.account.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {

  @Id
  @GeneratedValue
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String icon;

  @Column(nullable = false)
  private Integer ordinal = Integer.MAX_VALUE;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String description;

  @Column(length = 250)
  private String observation;

  @Column(nullable = false)
  private boolean archived = false;

  @Column(columnDefinition = "TEXT")
  private String options;

  @CreationTimestamp
  @Column(nullable = false)
  private Timestamp createdAt;

  @ManyToOne
  @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
  private Account createdBy;

  @UpdateTimestamp
  @Column(nullable = false)
  private Timestamp updatedAt;

  @ManyToOne
  @JoinColumn(name = "updated_by", referencedColumnName = "id", nullable = false)
  private Account updatedBy;

  public Project(@NotBlank String description) {
    this.description = description;
  }

}
