package io.github._2don.api.step;


import io.github._2don.api.utils.Convert;
import io.github._2don.api.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tasks/{taskId}/steps")
public class StepController {

  @Autowired
  private StepService stepService;

  @GetMapping
  public List<StepDTO> index(@AuthenticationPrincipal Long accountId,
                             @PathVariable Long projectId,
                             @PathVariable Long taskId) {
    return stepService.findSteps(accountId, projectId, taskId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public StepDTO store(@AuthenticationPrincipal Long accountId,
                       @PathVariable Long projectId,
                       @PathVariable Long taskId,
                       @RequestPart(name = "description") String description,
                       @RequestPart(name = "ordinal", required = false) Integer ordinal) {
    return stepService.create(accountId, projectId, taskId, description, ordinal);
  }

  @PatchMapping("/{stepId}")
  public StepDTO update(@AuthenticationPrincipal Long accountId,
                        @PathVariable Long projectId,
                        @PathVariable Long taskId,
                        @PathVariable Long stepId,
                        @RequestPart(name = "description", required = false) String description,
                        @RequestPart(name = "status", required = false) String status,
                        @RequestPart(name = "ordinal", required = false) Integer ordinal,
                        @RequestPart(name = "observation", required = false) String observation) {
    var _status = status == null
      ? null
      : Convert.toEnum(StepStatus.class, status).orElseThrow(Status.BAD_REQUEST);

    return stepService.update(accountId, projectId, taskId, stepId, description, _status, ordinal, observation);
  }

  @DeleteMapping("/{stepId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable Long projectId,
                      @PathVariable Long taskId,
                      @PathVariable Long stepId) {
    stepService.delete(accountId, projectId, taskId, stepId);
  }
}
