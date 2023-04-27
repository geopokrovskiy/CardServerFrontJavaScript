package com.geopokrovskiy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;


/**
 * В пакете dto (Data Transfer Object) создать generic класс ResponseResult с полями:
 * result типа boolean, message типа String, data типа T. Во всех API производить возврат json данного объекта,
 * не выгружая null поля. В случае успеха: result true, message null, data – тот объект,
 * который требуется вернуть в конкретном API.
 * В случае ошибки: result false, message c указанием ошибки, data null
 * @param <T>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> {
    private boolean result;
    private String message;
    private T data;

    public ResponseResult() {
    }

    public ResponseResult(boolean result, String message, T data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseResult<?> that = (ResponseResult<?>) o;
        return result == that.result && Objects.equals(message, that.message) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, message, data);
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "result=" + result +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
