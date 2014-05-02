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

    private final T t;
    private final K k;

    public Pair(T t, K k) {
        this.t = t;
        this.k = k;
    }

    public T getT() {
        return t;
    }

    public K getK() {
        return k;
    }

    @Override
    public String toString() {
        return "Pair{" + "t=" + t + ", k=" + k + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.t);
        hash = 13 * hash + Objects.hashCode(this.k);
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
        if( !Objects.equals(this.t, other.t) ){
            return false;
        }
        if( !Objects.equals(this.k, other.k) ){
            return false;
        }
        return true;
    }

}
