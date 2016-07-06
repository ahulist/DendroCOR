/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hulist.util;

import java.util.Objects;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 * @param <T>
 * @param <K>
 */
public class Pair<T, K> {

    private final T first;
    private final K second;

    public Pair(T t, K k) {
        this.first = t;
        this.second = k;
    }

    public T getFirst() {
        return first;
    }

    public K getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Pair{" + "First=" + first + ", Second=" + second + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.first);
        hash = 13 * hash + Objects.hashCode(this.second);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null ){
            return false;
        }
        if( getClass() != obj.getClass() ){
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if( !Objects.equals(this.first, other.first) ){
            return false;
        }
        if( !Objects.equals(this.second, other.second) ){
            return false;
        }
        return true;
    }

}
