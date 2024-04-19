package pl.ochnios.jpkloader.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ServiceResponse<T> success(T data) {
        return new ServiceResponse<T>(true, null, data);
    }

    public static <T> ServiceResponse<T> fail(String message) {
        return new ServiceResponse<T>(false, message, null);
    }
}
