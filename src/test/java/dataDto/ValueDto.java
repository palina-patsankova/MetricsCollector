package dataDto;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueDto {

    @Expose
    int id;
    @Expose
    String state;
    @Expose
    String startDate;
    @Expose
    String endDate;
    @Expose
    String name;
}
