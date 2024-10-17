package dataDto;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IssueDto {

    @Expose
    String key;
    @Expose
    FieldsDto fields;

}
