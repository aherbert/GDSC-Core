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

/**
 * Class for validating conditions.
 *
 * <p>Overloaded methods are provided for common argument types to avoid the performance overhead of
 * autoboxing and varargs array creation.
 */
public final class ValidationUtils {

  /** No public construction. */
  private ValidationUtils() {}

  /**
   * Return the specified object if it is not {@code null}, otherwise the default value.
   *
   * @param <T> the type of the reference
   * @param object the object reference to check for nullity
   * @param defaultValue the default value (must not be null)
   * @return {@code object} if not {@code null}, otherwise the default value
   * @throws NullPointerException if {@code defaultValue} is {@code null}
   */
  public static <T> T defaultIfNull(T object, T defaultValue) {
    return (object == null) ? checkNotNull(defaultValue, "Default value must not be null") : object;
  }

  /**
   * Check the {@code result} is {@code true}.
   *
   * @param result the result
   * @throws IllegalArgumentException If not {@code true}
   */
  public static void checkArgument(final boolean result) {
    if (!result) {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Check the {@code result} is {@code true}.
   *
   * <p>If not {@code true} the exception message is formed using {@link String#valueOf(Object)}.
   *
   * @param result the result
   * @param message the object used to form the exception message
   * @throws IllegalArgumentException If not {@code true}
   */
  public static void checkArgument(final boolean result, Object message) {
    if (!result) {
      throw new IllegalArgumentException(String.valueOf(message));
    }
  }

  /**
   * Check the {@code result} is {@code true}.
   *
   * <p>If not {@code true} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param result the result
   * @param format the format of the exception message
   * @param p1 the first argument of the exception message
   * @throws IllegalArgumentException If not {@code true}
   */
  public static void checkArgument(final boolean result, String format, int p1) {
    if (!result) {
      throw new IllegalArgumentException(String.format(format, p1));
    }
  }

  /**
   * Check the {@code result} is {@code true}.
   *
   * <p>If not {@code true} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param result the result
   * @param format the format of the exception message
   * @param p1 the first argument of the exception message
   * @throws IllegalArgumentException If not {@code true}
   */
  public static void checkArgument(final boolean result, String format, Object p1) {
    if (!result) {
      throw new IllegalArgumentException(String.format(format, p1));
    }
  }

  /**
   * Check the {@code result} is {@code true}.
   *
   * <p>If not {@code true} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param result the result
   * @param format the format of the exception message
   * @param p1 the first argument of the exception message
   * @param p2 the second argument of the exception message
   * @throws IllegalArgumentException If not {@code true}
   */
  public static void checkArgument(final boolean result, String format, int p1, int p2) {
    if (!result) {
      throw new IllegalArgumentException(String.format(format, p1, p2));
    }
  }

  /**
   * Check the {@code result} is {@code true}.
   *
   * <p>If not {@code true} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param result the result
   * @param format the format of the exception message
   * @param p1 the first argument of the exception message
   * @param p2 the second argument of the exception message
   * @throws IllegalArgumentException If not {@code true}
   */
  public static void checkArgument(final boolean result, String format, Object p1, Object p2) {
    if (!result) {
      throw new IllegalArgumentException(String.format(format, p1, p2));
    }
  }

  /**
   * Check the {@code result} is {@code true}.
   *
   * <p>If not {@code true} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param result the result
   * @param format the format of the exception message
   * @param args the arguments of the exception message
   * @throws IllegalArgumentException If not {@code true}
   */
  public static void checkArgument(final boolean result, String format, Object... args) {
    if (!result) {
      throw new IllegalArgumentException(String.format(format, args));
    }
  }

  /**
   * Check the {@code state} is {@code true}.
   *
   * @param state the state
   * @throws IllegalStateException If not {@code true}
   */
  public static void checkState(final boolean state) {
    if (!state) {
      throw new IllegalStateException();
    }
  }

  /**
   * Check the {@code state} is {@code true}.
   *
   * <p>If not {@code true} the exception message is formed using {@link String#valueOf(Object)}.
   *
   * @param state the state
   * @param message the object used to form the exception message
   * @throws IllegalStateException If not {@code true}
   */
  public static void checkState(final boolean state, Object message) {
    if (!state) {
      throw new IllegalStateException(String.valueOf(message));
    }
  }

  /**
   * Check the {@code state} is {@code true}.
   *
   * <p>If not {@code true} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param state the state
   * @param format the format of the exception message
   * @param p1 the first argument of the exception message
   * @throws IllegalStateException If not {@code true}
   */
  public static void checkState(final boolean state, String format, int p1) {
    if (!state) {
      throw new IllegalStateException(String.format(format, p1));
    }
  }

  /**
   * Check the {@code state} is {@code true}.
   *
   * <p>If not {@code true} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param state the state
   * @param format the format of the exception message
   * @param p1 the first argument of the exception message
   * @throws IllegalStateException If not {@code true}
   */
  public static void checkState(final boolean state, String format, Object p1) {
    if (!state) {
      throw new IllegalStateException(String.format(format, p1));
    }
  }

  /**
   * Check the {@code state} is {@code true}.
   *
   * <p>If not {@code true} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param state the state
   * @param format the format of the exception message
   * @param p1 the first argument of the exception message
   * @param p2 the second argument of the exception message
   * @throws IllegalStateException If not {@code true}
   */
  public static void checkState(final boolean state, String format, int p1, int p2) {
    if (!state) {
      throw new IllegalStateException(String.format(format, p1, p2));
    }
  }

  /**
   * Check the {@code state} is {@code true}.
   *
   * <p>If not {@code true} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param state the state
   * @param format the format of the exception message
   * @param p1 the first argument of the exception message
   * @param p2 the second argument of the exception message
   * @throws IllegalStateException If not {@code true}
   */
  public static void checkState(final boolean state, String format, Object p1, Object p2) {
    if (!state) {
      throw new IllegalStateException(String.format(format, p1, p2));
    }
  }

  /**
   * Check the {@code state} is {@code true}.
   *
   * <p>If not {@code true} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param state the state
   * @param format the format of the exception message
   * @param args the arguments of the exception message
   * @throws IllegalStateException If not {@code true}
   */
  public static void checkState(final boolean state, String format, Object... args) {
    if (!state) {
      throw new IllegalStateException(String.format(format, args));
    }
  }

  /**
   * Checks that the specified object reference is not {@code null}.
   *
   * <p>If not {@code true} the exception message is formed using {@link String#valueOf(Object)}.
   *
   * @param object the object reference to check for nullity
   * @param <T> the type of the reference
   * @return {@code object} if not {@code null}
   * @throws NullPointerException if {@code object} is {@code null}
   */
  public static <T> T checkNotNull(T object) {
    if (object == null) {
      throw new NullPointerException();
    }
    return object;
  }

  /**
   * Checks that the specified object reference is not {@code null}.
   *
   * @param <T> the type of the reference
   * @param object the object reference to check for nullity
   * @param message the object used to form the exception message
   * @return {@code object} if not {@code null}
   * @throws NullPointerException if {@code object} is {@code null}
   */
  public static <T> T checkNotNull(T object, Object message) {
    if (object == null) {
      throw new NullPointerException(String.valueOf(message));
    }
    return object;
  }

  /**
   * Checks that the specified object reference is not {@code null}.
   *
   * <p>If {@code null} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param <T> the type of the reference
   * @param object the object reference to check for nullity
   * @param format the format of the exception message
   * @param p1 the first argument of the exception message
   * @return {@code object} if not {@code null}
   * @throws NullPointerException if {@code object} is {@code null}
   */
  public static <T> T checkNotNull(T object, String format, int p1) {
    if (object == null) {
      throw new NullPointerException(String.format(format, p1));
    }
    return object;
  }

  /**
   * Checks that the specified object reference is not {@code null}.
   *
   * <p>If {@code null} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param <T> the type of the reference
   * @param object the object reference to check for nullity
   * @param format the format of the exception message
   * @param p1 the first argument of the exception message
   * @return {@code object} if not {@code null}
   * @throws NullPointerException if {@code object} is {@code null}
   */
  public static <T> T checkNotNull(T object, String format, Object p1) {
    if (object == null) {
      throw new NullPointerException(String.format(format, p1));
    }
    return object;
  }

  /**
   * Checks that the specified object reference is not {@code null}.
   *
   * <p>If {@code null} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param <T> the type of the reference
   * @param object the object reference to check for nullity
   * @param format the format of the exception message
   * @param p1 the first argument of the exception message
   * @param p2 the second argument of the exception message
   * @return {@code object} if not {@code null}
   * @throws NullPointerException if {@code object} is {@code null}
   */
  public static <T> T checkNotNull(T object, String format, int p1, int p2) {
    if (object == null) {
      throw new NullPointerException(String.format(format, p1, p2));
    }
    return object;
  }

  /**
   * Checks that the specified object reference is not {@code null}.
   *
   * <p>If {@code null} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param <T> the type of the reference
   * @param object the object reference to check for nullity
   * @param format the format of the exception message
   * @param p1 the first argument of the exception message
   * @param p2 the second argument of the exception message
   * @return {@code object} if not {@code null}
   * @throws NullPointerException if {@code object} is {@code null}
   */
  public static <T> T checkNotNull(T object, String format, Object p1, Object p2) {
    if (object == null) {
      throw new NullPointerException(String.format(format, p1, p2));
    }
    return object;
  }

  /**
   * Checks that the specified object reference is not {@code null}.
   *
   * <p>If {@code null} the exception message is formed using
   * {@link String#format(String, Object...)}.
   *
   * @param <T> the type of the reference
   * @param object the object reference to check for nullity
   * @param format the format of the exception message
   * @param args the arguments of the exception message
   * @return {@code object} if not {@code null}
   * @throws NullPointerException if {@code object} is {@code null}
   */
  public static <T> T checkNotNull(T object, String format, Object... args) {
    if (object == null) {
      throw new NullPointerException(String.format(format, args));
    }
    return object;
  }
}
