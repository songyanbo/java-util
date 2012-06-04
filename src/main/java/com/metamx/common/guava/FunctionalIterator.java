package com.metamx.common.guava;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.metamx.common.guava.nary.BinaryFn;
import com.metamx.common.guava.nary.BinaryTransformIterator;
import com.metamx.common.guava.nary.TrinaryFn;
import com.metamx.common.guava.nary.TrinaryTransformIterator;

import java.util.Iterator;

/**
 */
public class FunctionalIterator<T> implements Iterator<T>
{
  private final Iterator<T> delegate;

  public static <T> FunctionalIterator<T> create(Iterator<T> delegate)
  {
    return new FunctionalIterator<T>(delegate);
  }

  public static <T> FunctionalIterator<T> fromConcatenation(Iterator<T>... toConcat)
  {
    return new FunctionalIterator<T>(Iterators.concat(toConcat));
  }

  public static <T> FunctionalIterator<T> fromConcatenation(Iterator<Iterator<T>> toConcat)
  {
    return new FunctionalIterator<T>(Iterators.concat(toConcat));
  }

  public FunctionalIterator(
      Iterator<T> delegate
  )
  {
    this.delegate = delegate;
  }

  public boolean hasNext()
  {
    return delegate.hasNext();
  }

  public T next()
  {
    return delegate.next();
  }

  public void remove()
  {
    delegate.remove();
  }

  public <RetType> FunctionalIterator<RetType> transform(Function<T, RetType> fn)
  {
    return new FunctionalIterator<RetType>(Iterators.transform(delegate, fn));
  }

  public <RetType> FunctionalIterator<RetType> transformCat(Function<T, Iterator<RetType>> fn)
  {
    return new FunctionalIterator<RetType>(Iterators.concat(Iterators.transform(delegate, fn)));
  }

  public <RetType> FunctionalIterator<RetType> keep(Function<T, RetType> fn)
  {
    return new FunctionalIterator<RetType>(Iterators.filter(Iterators.transform(delegate, fn), Predicates.notNull()));
  }

  public FunctionalIterator<T> filter(Predicate<T> pred)
  {
    return new FunctionalIterator<T>(Iterators.filter(delegate, pred));
  }

  public FunctionalIterator<T> drop(int numToDrop)
  {
    return new FunctionalIterator<T>(new DroppingIterator<T>(delegate, numToDrop));
  }

  public FunctionalIterator<T> limit(int limit)
  {
    return new FunctionalIterator<T>(Iterators.limit(delegate, limit));
  }

  public FunctionalIterator<T> concat(Iterator<T>... toConcat)
  {
    if (toConcat.length == 1) {
      return new FunctionalIterator<T>(Iterators.concat(delegate, toConcat[0]));
    }
    return new FunctionalIterator<T>(Iterators.concat(delegate, Iterators.concat(toConcat)));
  }

  public FunctionalIterator<T> concat(Iterator<Iterator<T>> toConcat)
  {
    return new FunctionalIterator<T>(Iterators.concat(delegate, Iterators.concat(toConcat)));
  }

  public <InType, RetType> FunctionalIterator<RetType> binaryTransform(
      final Iterator<InType> otherIterator, final BinaryFn<T, InType, RetType> binaryFn
  )
  {
    return new FunctionalIterator<RetType>(BinaryTransformIterator.create(delegate, otherIterator, binaryFn));
  }

  public <InType1, InType2, RetType> FunctionalIterator<RetType> trinaryTransform(
      final Iterator<InType1> iterator1,
      final Iterator<InType2> iterator2,
      final TrinaryFn<T, InType1, InType2, RetType> trinaryFn
  )
  {
    return new FunctionalIterator<RetType>(TrinaryTransformIterator.create(delegate, iterator1, iterator2, trinaryFn));
  }
}