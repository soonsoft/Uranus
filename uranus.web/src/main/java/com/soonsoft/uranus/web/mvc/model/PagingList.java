package com.soonsoft.uranus.web.mvc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PagingList<T> implements List<T> {

    private final List<T> originalList;

    private final Integer pageTotal;

    private Integer pageIndex;

    private Integer pageSize;

    public PagingList(List<T> list) {
        this(list, list == null ? 0 : list.size());
    }

    public PagingList(List<T> list, int pageTotal) {
        this.originalList = list == null ? new ArrayList<T>(0) : list;
        this.pageTotal = pageTotal;
    }

    //#region List methods

    @Override
    public int size() {
        return originalList.size();
    }

    @Override
    public boolean isEmpty() {
        return originalList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return originalList.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return originalList.iterator();
    }

    @Override
    public Object[] toArray() {
        return originalList.toArray();
    }

    @Override
    public <R> R[] toArray(R[] a) {
        return originalList.toArray(a);
    }

    @Override
    public boolean add(T e) {
        return originalList.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return originalList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return originalList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return originalList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return originalList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return originalList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return originalList.retainAll(c);
    }

    @Override
    public void clear() {
        originalList.clear();
    }

    @Override
    public T get(int index) {
        return originalList.get(index);
    }

    @Override
    public T set(int index, T element) {
        return originalList.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        originalList.add(index, element);
    }

    @Override
    public T remove(int index) {
        return originalList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return originalList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return originalList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return originalList.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return originalList.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return originalList.subList(fromIndex, toIndex);
    }

    //#endregion
    
    @Override
    public String toString() {
        return originalList.toString();
    }

    public Integer getPageTotal() {
        return this.pageTotal;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

}
