package cs27.educate.howareyou.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FriendList {
    private final List<FriendWithNickname> logs;
}
