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

import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

//@formatter:off
/**
 * Copy of java.util.ArrayList but removing the checks for concurrent modification.
 *
 * @param <E>
 *            the element type
 */
public class TurboList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    private static final long serialVersionUID = -4227785171523226892L;

    /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer.
     */
    private transient Object[] elementData;

    /**
     * The size of the ArrayList (the number of elements it contains).
     */
    private int size;

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param  initialCapacity  the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    public TurboList(int initialCapacity) {
        super();
        if (initialCapacity < 0) {
          throw new IllegalArgumentException("Illegal Capacity: "+
                                             initialCapacity);
        }
        this.elementData = new Object[initialCapacity];
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public TurboList() {
        this(10);
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public TurboList(Collection<? extends E> c) {
        elementData = c.toArray();
        size = elementData.length;
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        if (elementData.getClass() != Object[].class) {
          elementData = Arrays.copyOf(elementData, size, Object[].class);
        }
    }

    /**
     * Trims the capacity of this <tt>TurboList</tt> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an <tt>TurboList</tt> instance.
     */
    public void trimToSize() {
        final int oldCapacity = elementData.length;
        if (size < oldCapacity) {
          elementData = Arrays.copyOf(elementData, size);
        }
    }

    /**
     * Increases the capacity of this <tt>TurboList</tt> instance, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     *
     * @param   minCapacity   the desired minimum capacity
     */
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > 0) {
          ensureCapacityInternal(minCapacity);
        }
    }

    private void ensureCapacityInternal(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - elementData.length > 0) {
          grow(minCapacity);
        }
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        final int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0) {
          newCapacity = minCapacity;
        }
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
          newCapacity = hugeCapacity(minCapacity);
        }
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) {
          throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this list contains no elements.
     *
     * @return <tt>true</tt> if this list contains no elements
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this list contains
     * at least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) {
              if (elementData[i]==null) {
                return i;
              }
            }
        } else {
          for (int i = 0; i < size; i++) {
            if (o.equals(elementData[i])) {
              return i;
            }
          }
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size-1; i >= 0; i--) {
              if (elementData[i]==null) {
                return i;
              }
            }
        } else {
          for (int i = size-1; i >= 0; i--) {
            if (o.equals(elementData[i])) {
              return i;
            }
          }
        }
        return -1;
    }

    /**
     * Returns a shallow copy of this <tt>TurboList</tt> instance.  (The
     * elements themselves are not copied.)
     *
     * @return a clone of this <tt>TurboList</tt> instance
     */
    @Override
    public Object clone() {
        try {
            final TurboList<?> v = (TurboList<?>) super.clone();
            v.elementData = Arrays.copyOf(elementData, size);
            return v;
        } catch (final CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list in
     *         proper sequence
     */
    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array.  If the list fits in the
     * specified array, it is returned therein.  Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of
     * this list.
     *
     * <p>If the list fits in the specified array with room to spare
     * (i.e., the array has more elements than the list), the element in
     * the array immediately following the end of the collection is set to
     * <tt>null</tt>.  (This is useful in determining the length of the
     * list <i>only</i> if the caller knows that the list does not contain
     * any null elements.)
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     * @throws NullPointerException if the specified array is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
          // Make a new array of a's runtime type, but my contents:
          return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        }
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size) {
          a[size] = null;
        }
        return a;
    }

    // Positional Access Operations

    /**
     * Get the element at the specified position.
     *
     * @param index
     *            the index
     * @return the element
     */
    @SuppressWarnings("unchecked")
    private E elementData(int index) {
        return (E) elementData[index];
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public E get(int index) {
        rangeCheck(index);

        return elementData(index);
    }

    /**
     * Fast returns the element at the specified position in this list.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException  if the index is out of range (index &lt; 0 || index &gt;= capacity)
     */
    public E getf(int index) throws IndexOutOfBoundsException {
        return elementData(index);
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        rangeCheck(index);

        final E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }

    /**
     * Fast replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @throws IndexOutOfBoundsException  if the index is out of range (index &lt; 0 || index &gt;= capacity)
     */
    public void setf(int index, E element) throws IndexOutOfBoundsException {
        elementData[index] = element;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    @Override
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);
        elementData[size++] = e;
        return true;
    }

    /**
     * Appends the specified element to the end of this list without capacity checks.
     *
     * @param e element to be appended to this list
     * @throws IndexOutOfBoundsException if there is no more capacity
     */
    public void addf(E e) {
        elementData[size++] = e;
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        rangeCheckForAdd(index);

        ensureCapacityInternal(size + 1);
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        elementData[index] = element;
        size++;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        rangeCheck(index);

        final E oldValue = elementData(index);

        final int numMoved = size - index - 1;
        if (numMoved > 0) {
          System.arraycopy(elementData, index+1, elementData, index,
                           numMoved);
        }
        elementData[--size] = null; // Let gc do its work

        return oldValue;
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++) {
              if (elementData[index] == null) {
                  fastRemove(index);
                  return true;
              }
            }
        } else {
          for (int index = 0; index < size; index++) {
            if (o.equals(elementData[index])) {
                fastRemove(index);
                return true;
            }
          }
        }
        return false;
    }

    /*
     * Private remove method that skips bounds checking and does not
     * return the value removed.
     */
    private void fastRemove(int index) {
        final int numMoved = size - index - 1;
        if (numMoved > 0) {
          System.arraycopy(elementData, index+1, elementData, index,
                           numMoved);
        }
        elementData[--size] = null; // Let gc do its work
    }

    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns.
     */
    @Override
    public void clear() {

        // Let gc do its work
        for (int i = 0; i < size; i++) {
          elementData[i] = null;
        }

        size = 0;
    }

    /**
     * Removes all of the elements from this list. Does not set the references to null to allow garbage collection.
     */
    public void clearf() {

        size = 0;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified collection is this list, and this
     * list is nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        final Object[] a = c.toArray();
        final int numNew = a.length;
        ensureCapacityInternal(size + numNew);
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Appends all of the elements in the specified list to the end of
     * this list.
     *
     * @param c list containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(TurboList<? extends E> c) {
        final Object[] a = c.elementData;
        final int numNew = c.size;
        ensureCapacityInternal(size + numNew);
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);

        final Object[] a = c.toArray();
        final int numNew = a.length;
        ensureCapacityInternal(size + numNew);

        final int numMoved = size - index;
        if (numMoved > 0) {
          System.arraycopy(elementData, index, elementData, index + numNew,
                           numMoved);
        }

        System.arraycopy(a, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     *
     * @throws IndexOutOfBoundsException if {@code fromIndex} or
     *         {@code toIndex} is out of range
     *         ({@code fromIndex < 0 ||
     *          fromIndex >= size() ||
     *          toIndex > size() ||
     *          toIndex < fromIndex})
     */
    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        final int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                         numMoved);

        // clear to let GC do its work
        final int newSize = size - (toIndex-fromIndex);
        for (int i = newSize; i < size; i++) {
          elementData[i] = null;
        }
        size = newSize;
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     */
    private void rangeCheck(int index) {
        if (index >= size) {
          throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    /**
     * A version of rangeCheck used by add and addAll.
     */
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0) {
          throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     *
     * @param c collection containing elements to be removed from this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see Collection#contains(Object)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return batchRemove(c, false);
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection.  In other words, removes from this list all
     * of its elements that are not contained in the specified collection.
     *
     * @param c collection containing elements to be retained in this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see Collection#contains(Object)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return batchRemove(c, true);
    }

    private boolean batchRemove(Collection<?> c, boolean complement) {
        final Object[] elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try {
            for (; r < size; r++) {
              if (c.contains(elementData[r]) == complement) {
                elementData[w++] = elementData[r];
              }
            }
        } finally {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            if (r != size) {
                System.arraycopy(elementData, r,
                                 elementData, w,
                                 size - r);
                w += size - r;
            }
            if (w != size) {
                // clear to let GC do its work
                for (int i = w; i < size; i++) {
                  elementData[i] = null;
                }
                modCount += size - w;
                size = w;
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Save the state of the <tt>TurboList</tt> instance to a stream (that
     * is, serialize it).
     *
     * @param s
     *            the output stream
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @serialData The length of the array backing the <tt>TurboList</tt>
     *             instance is emitted (int), followed by all of its elements
     *             (each an <tt>Object</tt>) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws IOException{
        // Write out element count, and any hidden stuff
        s.defaultWriteObject();

        // Write out array length
        s.writeInt(elementData.length);

        // Write out all elements in the proper order.
        for (int i=0; i<size; i++) {
          s.writeObject(elementData[i]);
        }
    }

    /**
     * Reconstitute the <tt>TurboList</tt> instance from a stream (that is,
     * deserialize it).
     *
     * @param s
     *            the input stream
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException
     *             the class not found exception
     */
    private void readObject(java.io.ObjectInputStream s)
        throws IOException, ClassNotFoundException {
        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in array length and allocate array
        final int arrayLength = s.readInt();
        final Object[] a = elementData = new Object[arrayLength];

        // Read in all elements in the proper order.
        for (int i=0; i<size; i++) {
          a[i] = s.readObject();
        }
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * The specified index indicates the first element that would be
     * returned by an initial call to {@link ListIterator#next next}.
     * An initial call to {@link ListIterator#previous previous} would
     * return the element with the specified index minus one.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index > size) {
          throw new IndexOutOfBoundsException("Index: "+index);
        }
        return new ListItr(index);
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
     *
     * @see #listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator() {
        return new ListItr(0);
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * An optimized version of AbstractList.Itr
     */
    private class Itr implements Iterator<E> {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            final int i = cursor;
            if (i >= size) {
              throw new NoSuchElementException();
            }
            final Object[] elementData = TurboList.this.elementData;
            if (i >= elementData.length) {
              throw new ConcurrentModificationException();
            }
            cursor = i + 1;
            return (E) elementData[lastRet = i];
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
              throw new IllegalStateException();
            }

            try {
                TurboList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
            } catch (final IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * An optimized version of AbstractList.ListItr
     */
    private class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
            super();
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E previous() {
            final int i = cursor - 1;
            if (i < 0) {
              throw new NoSuchElementException();
            }
            final Object[] elementData = TurboList.this.elementData;
            if (i >= elementData.length) {
              throw new ConcurrentModificationException();
            }
            cursor = i;
            return (E) elementData[lastRet = i];
        }

        @Override
        public void set(E e) {
            if (lastRet < 0) {
              throw new IllegalStateException();
            }
            try {
                TurboList.this.set(lastRet, e);
            } catch (final IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void add(E e) {
            try {
                final int i = cursor;
                TurboList.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
            } catch (final IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * Returns a view of the portion of this list between the specified
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.  (If
     * {@code fromIndex} and {@code toIndex} are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations.
     *
     * <p>This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).  Any operation that expects
     * a list can be used as a range operation by passing a subList view
     * instead of a whole list.  For example, the following idiom
     * removes a range of elements from a list:
     * <pre>
     *      list.subList(from, to).clear();
     * </pre>
     * Similar idioms may be constructed for {@link #indexOf(Object)} and
     * {@link #lastIndexOf(Object)}, and all of the algorithms in the
     * {@link Collections} class can be applied to a subList.
     *
     * <p>The semantics of the list returned by this method become undefined if
     * the backing list (i.e., this list) is <i>structurally modified</i> in
     * any way other than via the returned list.  (Structural modifications are
     * those that change the size of this list, or otherwise perturb it in such
     * a fashion that iterations in progress may yield incorrect results.)
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size);
        return new SubList(this, 0, fromIndex, toIndex);
    }

    private static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0) {
          throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }
        if (toIndex > size) {
          throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        }
        if (fromIndex > toIndex) {
          throw new IllegalArgumentException("fromIndex(" + fromIndex +
                                             ") > toIndex(" + toIndex + ")");
        }
    }

    private class SubList extends AbstractList<E> implements RandomAccess {
        private final AbstractList<E> parent;
        private final int parentOffset;
        private final int offset;
        int size;

        SubList(AbstractList<E> parent,
                int offset, int fromIndex, int toIndex) {
            this.parent = parent;
            this.parentOffset = fromIndex;
            this.offset = offset + fromIndex;
            this.size = toIndex - fromIndex;
        }

        @Override
        public E set(int index, E e) {
            rangeCheck(index);
            final E oldValue = TurboList.this.elementData(offset + index);
            TurboList.this.elementData[offset + index] = e;
            return oldValue;
        }

        @Override
        public E get(int index) {
            rangeCheck(index);
            return TurboList.this.elementData(offset + index);
        }

        @Override
        public int size() {
            return this.size;
        }

        @Override
        public void add(int index, E e) {
            rangeCheckForAdd(index);
            parent.add(parentOffset + index, e);
            this.size++;
        }

        @Override
        public E remove(int index) {
            rangeCheck(index);
            final E result = parent.remove(parentOffset + index);
            this.size--;
            return result;
        }

        @Override
        @SuppressWarnings("rawtypes")
        protected void removeRange(int fromIndex, int toIndex) {
            if (parent instanceof TurboList) {
            ((TurboList)parent)
                      .removeRange(parentOffset + fromIndex,
                                   parentOffset + toIndex);
          } else if (parent instanceof TurboList.SubList) {
        ((TurboList.SubList)parent)
                  .removeRange(parentOffset + fromIndex,
                               parentOffset + toIndex);
      } else {
        // This should not happen since this is a private class
        super.removeRange(parentOffset + fromIndex,
                          parentOffset + toIndex);
      }
            this.size -= toIndex - fromIndex;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return addAll(this.size, c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            rangeCheckForAdd(index);
            final int cSize = c.size();
            if (cSize==0) {
              return false;
            }

            parent.addAll(parentOffset + index, c);
            this.size += cSize;
            return true;
        }

        @Override
        public Iterator<E> iterator() {
            return listIterator();
        }

        @Override
        public ListIterator<E> listIterator(final int index) {
            rangeCheckForAdd(index);
            final int offset = this.offset;

            return new ListIterator<E>() {
                int cursor = index;
                int lastRet = -1;

                @Override
                public boolean hasNext() {
                    return cursor != SubList.this.size;
                }

                @Override
                @SuppressWarnings("unchecked")
                public E next() {
                    final int i = cursor;
                    if (i >= SubList.this.size) {
                      throw new NoSuchElementException();
                    }
                    final Object[] elementData = TurboList.this.elementData;
                    if (offset + i >= elementData.length) {
                      throw new ConcurrentModificationException();
                    }
                    cursor = i + 1;
                    return (E) elementData[offset + (lastRet = i)];
                }

                @Override
                public boolean hasPrevious() {
                    return cursor != 0;
                }

                @Override
                @SuppressWarnings("unchecked")
                public E previous() {
                    final int i = cursor - 1;
                    if (i < 0) {
                      throw new NoSuchElementException();
                    }
                    final Object[] elementData = TurboList.this.elementData;
                    if (offset + i >= elementData.length) {
                      throw new ConcurrentModificationException();
                    }
                    cursor = i;
                    return (E) elementData[offset + (lastRet = i)];
                }

                @Override
                public int nextIndex() {
                    return cursor;
                }

                @Override
                public int previousIndex() {
                    return cursor - 1;
                }

                @Override
                public void remove() {
                    if (lastRet < 0) {
                      throw new IllegalStateException();
                    }
                    try {
                        SubList.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                    } catch (final IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                @Override
                public void set(E e) {
                    if (lastRet < 0) {
                      throw new IllegalStateException();
                    }
                    try {
                        TurboList.this.set(offset + lastRet, e);
                    } catch (final IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                @Override
                public void add(E e) {
                    try {
                        final int i = cursor;
                        SubList.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                    } catch (final IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }
            };
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList(this, offset, fromIndex, toIndex);
        }

        private void rangeCheck(int index) {
            if (index < 0 || index >= this.size) {
              throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
            }
        }

        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > this.size) {
              throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
            }
        }

        private String outOfBoundsMsg(int index) {
            return "Index: "+index+", Size: "+this.size;
        }
    }

    /**
     * Represents a predicate (boolean-valued function) of one argument.
     * <p>
     * This functionality was added in Java 1.8 so for backward compatibility
     * to Java 1.5 the required methods from the java.util.function.Predicate
     * interface have been duplicated here.
     *
     * @param <T>
     *            the generic type
     */
    @FunctionalInterface
    public interface SimplePredicate<T> {

        /**
         * Evaluates this predicate on the given argument.
         *
         * @param argument the input argument
         * @return {@code true} if the input argument matches the predicate,
         * otherwise {@code false}
         */
        public boolean test(T argument);
    }

    /**
     * Removes from the list the if an element passes the filter.
     *
     * @param filter
     *            the filter
     * @return true, if the list was modified
     */
    public boolean removeIf(SimplePredicate<? super E> filter) {
        Objects.requireNonNull(filter);
        // figure out which elements are to be removed
        // any exception thrown from the filter predicate at this stage
        // will leave the collection unmodified
        int removeCount = 0;
        final BitSet removeSet = new BitSet(size);
        final int size = this.size;
        for (int i=0; i < size; i++) {
            @SuppressWarnings("unchecked")
            final E element = (E) elementData[i];
            if (filter.test(element)) {
                removeSet.set(i);
                removeCount++;
            }
        }

        // shift surviving elements left over the spaces left by removed elements
        final boolean anyToRemove = removeCount > 0;
        if (anyToRemove) {
            final int newSize = size - removeCount;
            for (int i=0, j=0; (i < size) && (j < newSize); i++, j++) {
                i = removeSet.nextClearBit(i);
                elementData[j] = elementData[i];
            }
            for (int k=newSize; k < size; k++)
             {
              elementData[k] = null;  // Let gc do its work
            }
            this.size = newSize;
        }

        return anyToRemove;
    }

    /**
     * Reverse the list contents.
     */
    public void reverse()
    {
        for (int left=0, mid=size>>1, right=size-1; left<mid; left++, right--)
        {
            // swap the values at the left and right indices
            final Object temp = elementData[left];
            elementData[left] = elementData[right];
            elementData[right] = temp;
        }
    }

    /**
     * Sort the list.
     *
     * @param c the comparator
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void sort(Comparator<? super E> c)
    {
        Arrays.sort(elementData, 0, size, (Comparator)c);
    }
}
//@formatter:on
