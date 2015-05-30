package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by eu on 1/30/2015.
 */
public class DataApiResult<T> {

    @JsonCreator
    public DataApiResult(@JsonProperty("result") T result,@JsonProperty("Error") String error) {
        this.Result = result;
    }

    public T Result;
    public String Error;

    public T getResult() {
        return Result;
    }

    public void setResult(T result) {
        Result = result;
    }
    public String getError() {
        return Error;
    }

    public void setError(String error) {
        Error = error;
    }
}
