package ar.edu.iw3.auth.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSlimV1Response {
    private String username;
    private String email;
    private List<String> roles;
}
