package be.kdg.integration5.gameplatformcontext.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FriendRequest {
    private UUID requestUUID;
    private Player sender;
    private Player receiver;
    private Status status;

    public enum Status {
        PENDING, ACCEPTED, DECLINED
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(requestUUID, that.requestUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(requestUUID);
    }
}
