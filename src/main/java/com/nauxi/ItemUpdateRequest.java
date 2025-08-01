package com.nauxi;

public class ItemUpdateRequest {
    public final String type;
    public final String id;

    public ItemUpdateRequest(String type, String id) {
        this.type = type;
        this.id = id;
    }
}
