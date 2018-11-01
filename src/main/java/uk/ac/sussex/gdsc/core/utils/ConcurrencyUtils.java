/*-
 * #%L
 * Genome Damage and Stability Centre ImageJ Core Package
 *
 * Contains code used by:
 *
 * GDSC ImageJ Plugins - Microscopy image analysis
 *
 * GDSC SMLM ImageJ Plugins - Single molecule localisation microscopy (SMLM)
 * %%
 * Copyright (C) 2011 - 2018 Alex Herbert
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package uk.ac.sussex.gdsc.core.utils;

import uk.ac.sussex.gdsc.core.data.AsynchronousException;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Contains concurrent utility functions.
 */
public final class ConcurrencyUtils {

  /**
   * No public construction.
   */
  private ConcurrencyUtils() {}

  /**
   * Waits for all threads to complete computation.
   *
   * @param futures the futures
   * @throws InterruptedException the interrupted exception
   * @throws ExecutionException the execution exception
   */
  public static void waitForCompletion(List<Future<?>> futures)
      throws InterruptedException, ExecutionException {
    for (final Future<?> f : futures) {
      f.get();
    }
  }

  /**
   * Waits for all threads to complete computation.
   *
   * @param futures the futures
   * @throws AsynchronousException a wrapped InterruptedException or ExecutionException
   * @see #waitForCompletionOrError(List, Consumer)
   */
  public static void waitForCompletionOrError(List<Future<?>> futures) {
    waitForCompletionOrError(futures, null);
  }

  /**
   * Waits for all threads to complete computation.
   *
   * <p>This is convenience method that wraps an {@link InterruptedException} with an
   * {@link AsynchronousException}. Note: If an {@link InterruptedException} occurs the thread
   * interrupted state is reset.
   *
   * <p>If an {@link ExecutionException} occurs and the cause is an unchecked exception then cause
   * will be re-thrown. Otherwise wraps the cause with an {@link AsynchronousException}.
   *
   * <p>If not null, the error handler will be passed the original caught exception, either
   * {@link InterruptedException} or {@link ExecutionException} to preserve the stack trace.
   *
   * @param futures the futures
   * @param errorHandler the error handler used to process the exception (if not null)
   * @throws AsynchronousException a wrapped InterruptedException or ExecutionException
   */
  public static void waitForCompletionOrError(List<Future<?>> futures,
      Consumer<Exception> errorHandler) {
    try {
      for (final Future<?> f : futures) {
        f.get();
      }
    } catch (InterruptedException ex) {
      // Restore interrupted state...
      Thread.currentThread().interrupt();
      handleError(errorHandler, ex);
      throw new AsynchronousException(ex);
    } catch (ExecutionException ex) {
      handleErrorAndRethrow(errorHandler, ex);
    }
  }

  /**
   * Handle the error using the error handler and re-throw the underlying unchecked exception or a
   * wrapped exception.
   *
   * @param errorHandler the error handler
   * @param exception the exception
   */
  private static void handleErrorAndRethrow(Consumer<Exception> errorHandler, Exception exception) {
    handleError(errorHandler, exception);
    final Throwable cause = exception.getCause();
    // Note: Instance of is false for null
    if (cause instanceof RuntimeException) {
      throw (RuntimeException) cause;
    }
    if (cause instanceof Error) {
      throw (Error) cause;
    }
    throw new AsynchronousException((cause != null) ? cause : exception);
  }

  /**
   * Handle the error using the error handler.
   *
   * @param errorHandler the error handler
   * @param exception the exception
   */
  private static void handleError(Consumer<Exception> errorHandler, Exception exception) {
    if (errorHandler != null) {
      errorHandler.accept(exception);
    }
  }
}
