package dataDto;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SprintInfoResponse {

    @Expose
    List<ValueDto> values;
    @Expose
    int total;
}
