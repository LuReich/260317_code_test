package backend.common;

public record ApiError(
    String code,
    String message
) {}

