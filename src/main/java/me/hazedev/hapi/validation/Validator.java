package me.hazedev.hapi.validation;

import java.util.List;

public interface Validator<T> {

    T validateValue(String value) throws IllegalArgumentException;

    List<String> getPossibleValues(String search);

}
