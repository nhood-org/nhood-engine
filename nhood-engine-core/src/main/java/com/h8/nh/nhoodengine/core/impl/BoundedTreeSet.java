package com.h8.nh.nhoodengine.core.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

final class BoundedTreeSet<E> extends TreeSet<E> {

    private static final long serialVersionUID = 64L;

    private final int limit;

    BoundedTreeSet(final int limit, final Comparator<? super E> comparator) {
        super(comparator);
        this.limit = limit;
    }

    @Override
    public boolean add(final E e) {
        boolean result = super.add(e);
        dropTail();
        return result;
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        boolean result = super.addAll(c);
        dropTail();
        return result;
    }

    private void dropTail() {
        if (size() >= limit) {
            for (int i = 0; i < size() - limit; i++) {
                remove(last());
            }
        }
    }
}
