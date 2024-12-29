package practicum.util;

import java.time.LocalDateTime;

public record ErrorResponse(String message, String reason, String status, LocalDateTime timestamp) {
}
