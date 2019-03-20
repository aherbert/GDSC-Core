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
 * Copyright (C) 2011 - 2019 Alex Herbert
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

package uk.ac.sussex.gdsc.core.utils.concurrent;

import uk.ac.sussex.gdsc.core.annotation.NotNull;

import org.apache.commons.lang3.concurrent.ConcurrentRuntimeException;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
   * Waits for all threads to complete computation. Transforms checked exceptions into runtime
   * exceptions.
   *
   * @param futures the futures
   * @throws ConcurrentRuntimeException a wrapped InterruptedException or ExecutionException
   * @see #waitForCompletionUnchecked(List, Consumer)
   */
  public static void waitForCompletionUnchecked(List<Future<?>> futures) {
    waitForCompletionUnchecked(futures, null);
  }

  /**
   * Waits for all threads to complete computation. Transforms checked exceptions into runtime
   * exceptions.
   *
   * <p>This is convenience method that wraps an {@link InterruptedException} with an
   * {@link ConcurrentRuntimeException}. Note: If an {@link InterruptedException} occurs the thread
   * interrupted state is reset.
   *
   * <p>If an {@link ExecutionException} occurs and the cause is an unchecked exception then cause
   * will be re-thrown. Otherwise wraps the cause with an {@link ConcurrentRuntimeException}.
   *
   * <p>If not null, the error handler will be passed the original caught exception, either
   * {@link InterruptedException} or {@link ExecutionException} to preserve the stack trace.
   *
   * @param futures the futures
   * @param errorHandler the error handler used to process the exception (can be null)
   * @throws ConcurrentRuntimeException a wrapped InterruptedException or ExecutionException
   */
  public static void waitForCompletionUnchecked(List<Future<?>> futures,
      Consumer<Exception> errorHandler) {
    try {
      for (final Future<?> f : futures) {
        f.get();
      }
    } catch (final InterruptedException ex) {
      handleError(errorHandler, ex);
      // Restore interrupted state...
      Thread.currentThread().interrupt();
      throw new ConcurrentRuntimeException(ex);
    } catch (final ExecutionException ex) {
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
    throw new ConcurrentRuntimeException((cause == null) ? exception : cause);
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

  /**
   * Executes the given tasks, returning a list of Futures holding their status and results when all
   * complete. {@link Future#isDone} is {@code true} for each element of the returned list.
   *
   * <p>The result must still be checked using {@link Future#get()} to determine if an exception
   * occurred in each task. Transforms checked exceptions into runtime exceptions.
   *
   * @param <T> the type of the values returned from the tasks
   * @param executor the executor
   * @param tasks the tasks
   * @return the list
   * @throws ConcurrentRuntimeException a wrapped InterruptedException or ExecutionException
   * @see ExecutorService#invokeAll(Collection)
   * @see #invokeAllUnchecked(ExecutorService, Collection, Consumer)
   */
  public static <T> List<Future<T>> invokeAllUnchecked(ExecutorService executor,
      Collection<? extends Callable<T>> tasks) {
    return invokeAllUnchecked(executor, tasks, null);
  }

  /**
   * Executes the given tasks using the executor, returning a list of Futures holding their status
   * and results when all complete. {@link Future#isDone} is {@code true} for each element of the
   * returned list.
   *
   * <p>The result must still be checked using {@link Future#get()} to determine if an exception
   * occurred in each task. Transforms checked exceptions into runtime exceptions.
   *
   * <p>This is convenience method that wraps an {@link InterruptedException} with an
   * {@link ConcurrentRuntimeException}. Note: If an {@link InterruptedException} occurs the thread
   * interrupted state is reset.
   *
   * @param <T> the type of the values returned from the tasks
   * @param executor the executor
   * @param tasks the tasks
   * @param errorHandler the error handler used to process the exception (can be null)
   * @return a list of Futures representing the tasks, in the same sequential order as produced by
   *         the iterator f
   * @throws ConcurrentRuntimeException a wrapped InterruptedException
   * @see ExecutorService#invokeAll(Collection)
   */
  public static <T> List<Future<T>> invokeAllUnchecked(ExecutorService executor,
      Collection<? extends Callable<T>> tasks, Consumer<Exception> errorHandler) {
    try {
      return executor.invokeAll(tasks);
    } catch (final InterruptedException ex) {
      handleError(errorHandler, ex);
      // Restore interrupted state...
      Thread.currentThread().interrupt();
      throw new ConcurrentRuntimeException(ex);
    }
  }

  /**
   * Refresh the object reference.
   *
   * <p>Test the object in the provided reference with the given test:</p>
   *
   * <ul>
   *
   * <li>If the object is not {@code null} and it passes the test then return the object.</li>
   *
   * <li>If it is {@code null} or it fails the test then generate a new value with the supplier, set
   * that in the reference and return the new value. The old value is discarded. Note: If the
   * supplier creates a {@code null} object then an exception is thrown. </li>
   *
   * </ul>
   *
   * <p>Note that the object passed to the test will not be {@code null} and the test need not
   * handle this case.
   *
   * <p>The value returned will match the value that is left in the reference. It will not be
   * {@code null}.
   *
   * <p>Note: The entire operation is atomic. If there is contention among threads the supplier
   * and test may be invoked multiple times so should be side-effect-free.
   *
   * @param <T> the generic type
   * @param reference the reference (must not be null)
   * @param test the test (must not be null)
   * @param supplier the supplier (must not be null)
   * @return the value
   * @throws NullPointerException If the {@link Supplier} creates a null object
   */
  @SuppressWarnings("null")
  @NotNull
  public static <T> T refresh(AtomicReference<T> reference, final Predicate<T> test,
      final Supplier<T> supplier) {
    return reference.updateAndGet(value -> {
      if (value != null && test.test(value)) {
        return value;
      }
      return Objects.requireNonNull(supplier.get(), "Generated object is null");
    });
  }
}
