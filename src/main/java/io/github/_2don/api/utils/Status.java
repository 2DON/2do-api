package io.github._2don.api.utils;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

public class Status {

  /**
   * Cannot find object, relationship.
   */
  public static final Supplier<ResponseStatusException>
    NOT_FOUND = supplyRSE(HttpStatus.NOT_FOUND),

  /**
   * The request is wrong.
   */
  BAD_REQUEST = supplyRSE(HttpStatus.BAD_REQUEST),

  /**
   * Already been used.
   */
  CONFLICT = supplyRSE(HttpStatus.CONFLICT),

  /**
   * Some concurrent connection invalidated the data, and is GONE.
   */
  GONE = supplyRSE(HttpStatus.GONE),

  /**
   * The resource is locked.
   */
  LOCKED = supplyRSE(HttpStatus.LOCKED),

  /**
   * Insufficient permission, wrong password.
   */
  UNAUTHORIZED = supplyRSE(HttpStatus.UNAUTHORIZED),

  /**
   * Not a premium.
   */
  UPGRADE_REQUIRED = supplyRSE(HttpStatus.UPGRADE_REQUIRED),

  /**
   * The dev made a mistake.
   */
  INTERNAL_SERVER_ERROR = supplyRSE(HttpStatus.INTERNAL_SERVER_ERROR);

  private Status() {
  }

  /**
   * Returns a {@code Supplier<ResponseStatusException>} with the desired status code.
   *
   * @param status
   * @return
   * @see ResponseStatusException
   */
  @NonNull
  public static Supplier<ResponseStatusException> supplyRSE(@NonNull HttpStatus status) {
    return () -> new ResponseStatusException(status);
  }

}
