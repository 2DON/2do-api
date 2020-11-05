package io.github._2don.api.step;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/tasks/{taskId}/steps")
public class StepController {

  @Autowired
  private StepService stepService;

  @GetMapping
  public List<Step> index(@AuthenticationPrincipal Long accountId, @PathVariable Long projectId,
      @PathVariable Long taskId) {
    return stepService.getSteps(accountId, projectId, taskId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Step store(@AuthenticationPrincipal Long accountId, @Validated @RequestBody Step step,
      @PathVariable Long projectId, @PathVariable Long taskId) {
    return stepService.add(accountId, step, projectId, taskId);
  }

  @GetMapping("/{stepId}")
  public Step show(@AuthenticationPrincipal Long accountId, @PathVariable Long stepId,
      @PathVariable Long projectId, @PathVariable Long taskId) {
    return stepService.getStep(accountId, stepId, projectId, taskId);
  }

  @PatchMapping("/{stepId}")
  public Step edit(@AuthenticationPrincipal Long accountId, @PathVariable Long stepId,
      @PathVariable Long projectId, @PathVariable Long taskId, @RequestBody Step step) {
    return stepService.edit(accountId, stepId, projectId, taskId, step);
  }

  @DeleteMapping("/{stepId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId, @PathVariable Long stepId,
      @PathVariable Long projectId, @PathVariable Long taskId) {

    stepService.delete(accountId, stepId, projectId, taskId);
  }
}
