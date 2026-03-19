package backend.common;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.Instant;

@JsonPropertyOrder({"status", "date", "content"})
public record ApiResponse<T>(
    int status,
    String date,
    T content
) {
    public static <T> ApiResponse<T> ok(T content) {
        return new ApiResponse<>(200, Instant.now().toString(), content);
    }

    public static <T> ApiResponse<T> created(T content) {
        return new ApiResponse<>(201, Instant.now().toString(), content);
    }

    public static <T> ApiResponse<T> error(int status, T message) {
        return new ApiResponse<>(status, Instant.now().toString(), message);
    }
}

