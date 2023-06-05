package cs27.educate.howareyou.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FriendWithNickname {
    private String fuid;
    private String fnickname;
    private String fteamid;
    private int fstatus;
    private String fsupport;
    private int fhelp;
}
