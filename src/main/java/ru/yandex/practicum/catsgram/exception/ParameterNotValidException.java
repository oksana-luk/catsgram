package ru.yandex.practicum.catsgram.exception;

public class ParameterNotValidException extends IllegalArgumentException {
    private final String reason;
    private final String parameter;

    public ParameterNotValidException(String parameter, String reason) {
        this.parameter = parameter;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public String getParameter() {
        return parameter;
    }
}
