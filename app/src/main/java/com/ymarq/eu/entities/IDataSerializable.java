package com.ymarq.eu.entities;

/**
 * Created by eu on 2/21/2015.
 */
public interface IDataSerializable<T> {
    public String getAsJson();
    T getFromJson();
}
