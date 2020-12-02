package io.github._2don.api.stats;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Date;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectDayStats {

  private Date date;
  private Long created = 0L;
  private Long updated = 0L;
  private HashMap<Long, Long> createdBy = new HashMap<>();
  private HashMap<Long, Long> updatedBy = new HashMap<>();

  public void createdPlusOne(@NonNull Long accountId) {
    var count = createdBy.containsKey(accountId) ? createdBy.get(accountId) : 0;
    createdBy.put(accountId, count + 1);

    created += 1;
  }

  public void updatedPlusOne(@NonNull Long accountId) {
    var count = updatedBy.containsKey(accountId) ? updatedBy.get(accountId) : 0;
    updatedBy.put(accountId, count + 1);

    updated += 1;
  }

}
