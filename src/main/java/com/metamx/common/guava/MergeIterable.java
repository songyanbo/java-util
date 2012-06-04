package com.metamx.common.guava;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

/**
 */
public class MergeIterable<T> implements Iterable<T>
{
  private final Comparator<T> comparator;
  private final Iterable<Iterable<T>> baseIterables;

  public MergeIterable(
      Comparator<T> comparator,
      Iterable<Iterable<T>> baseIterables
  )
  {
    this.comparator = comparator;
    this.baseIterables = baseIterables;
  }

  @Override
  public Iterator<T> iterator()
  {
    final PriorityQueue<PeekingIterator<T>> pQueue = new PriorityQueue<PeekingIterator<T>>(
        16,
        new Comparator<PeekingIterator<T>>()
        {
          @Override
          public int compare(PeekingIterator<T> lhs, PeekingIterator<T> rhs)
          {
            return comparator.compare(lhs.peek(), rhs.peek());
          }
        }
    );

    for (Iterable<T> baseIterable : baseIterables) {
      final PeekingIterator<T> iter = Iterators.peekingIterator(baseIterable.iterator());

      if (iter.hasNext()) {
        pQueue.add(iter);
      }
    }

    return new Iterator<T>()
    {
      @Override
      public boolean hasNext()
      {
        return ! pQueue.isEmpty();
      }

      @Override
      public T next()
      {
        if (! hasNext()) {
          throw new NoSuchElementException();
        }

        PeekingIterator<T> retIt = pQueue.remove();
        T retVal = retIt.next();

        if (retIt.hasNext()) {
          pQueue.add(retIt);
        }

        return retVal;
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
}