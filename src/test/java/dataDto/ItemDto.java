package dataDto;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDto {

    @Expose
    String field;
    @Expose
    String fromString;
    @Expose
    String toString;

}
