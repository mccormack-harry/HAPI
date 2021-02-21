package me.hazedev.hapi.command;

import me.hazedev.hapi.validation.Validator;

import java.util.Objects;

public class Argument<T> {

    private final Validator<T> validator;
    private final String prompt;
    private final T defaultValue;

    public Argument(Validator<T> validator, String prompt, T defaultValue) {
        this.validator = validator;
        this.prompt = Objects.requireNonNull(prompt);
        this.defaultValue = defaultValue;
    }

    public Argument(Validator<T> validator, String prompt) {
        this(validator, prompt, null);
    }

    public Argument(String prompt, T defaultValue) {
        this(null, prompt, defaultValue);
    }

    public Argument(String prompt) {
        this(null, prompt, null);
    }

    public Validator<T> getValidator() {
        return validator;
    }

    public String getPrompt() {
        return prompt;
    }

    public boolean isOptional() {
        return defaultValue != null;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

}
