/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

/**
 *
 * @author rlittle
 * @param <K>
 * @param <V>
 */
public class SelectItem<K,V> {
    private final K key;
    private final V value;
    
    public SelectItem(K key, V value) {
        this.key=key;
        this.value=value;
    }
    
    public K getKey() {
        return key;
    }
    
    public V getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return (String) value;
    }
}
