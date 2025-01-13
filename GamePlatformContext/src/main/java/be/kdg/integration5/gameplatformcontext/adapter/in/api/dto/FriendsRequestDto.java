package be.kdg.integration5.gameplatformcontext.adapter.in.api.dto;

import java.util.UUID;

public class FriendsRequestDto {
    private UUID senderId;
    private UUID receiverId;

    // Getters and Setters
    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }
}
