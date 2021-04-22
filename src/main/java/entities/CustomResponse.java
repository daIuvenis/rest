package entities;
import lombok.*;
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomResponse {
    private int code;
    private String message;
    private String type;
}
