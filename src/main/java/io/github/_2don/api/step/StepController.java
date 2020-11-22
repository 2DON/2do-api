package io.github._2don.api.step;


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
  public List<Step> index(@AuthenticationPrincipal Long accountId,
                          @PathVariable Long projectId,
                          @PathVariable Long taskId) {
    return stepService.getSteps(accountId, projectId, taskId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Step store(@AuthenticationPrincipal Long accountId,
                    @RequestParam(name = "description", required = true) String description,
                    @PathVariable Long projectId,
                    @PathVariable Long taskId) {
    // return stepService.add(accountId, step, projectId, taskId);
    return stepService.add(accountId, description, projectId, taskId);
  }

  @GetMapping("/{stepId}")
  public Step show(@AuthenticationPrincipal Long accountId,
                   @PathVariable Long stepId,
                   @PathVariable Long projectId,
                   @PathVariable Long taskId) {
    return stepService.getStep(accountId, stepId, projectId, taskId);
  }

  @PatchMapping("/{stepId}")
  public Step edit(@AuthenticationPrincipal Long accountId,
                   @PathVariable Long stepId,
                   @PathVariable Long projectId,
                   @PathVariable Long taskId,
                   @RequestParam(name = "ordinal", required = false) Integer ordinal,
                   @RequestParam(name = "description", required = false) String description,
                   @RequestParam(name = "observation", required = false) String observation,
                   @RequestParam(name = "status", required = false) String status) {
    // return stepService.edit(accountId, stepId, projectId, taskId, step);
    return stepService.edit(accountId, stepId, projectId, taskId, ordinal, description, observation,
      status);
  }

  @DeleteMapping("/{stepId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable Long stepId,
                      @PathVariable Long projectId,
                      @PathVariable Long taskId) {

    stepService.delete(accountId, stepId, projectId, taskId);
  }
}
