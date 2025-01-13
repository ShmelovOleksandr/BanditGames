package be.kdg.integration5.gameplatformcontext.adapter.in.api.dto;

import java.util.UUID;

public class FriendsRequestResponseDTO {
    private UUID requestId;
    private boolean accepted;

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
