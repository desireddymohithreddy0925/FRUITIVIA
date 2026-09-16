package com.fruitivia.inventory.event;

import com.fruitivia.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class InventoryLowEvent extends DomainEvent {
    private final UUID fruitId;
    private final UUID varietyId;

    public InventoryLowEvent(Object source, UUID fruitId, UUID varietyId) {
        super(source);
        this.fruitId = fruitId;
        this.varietyId = varietyId;
    }

    @Override
    public String getEventType() {
        return "InventoryLowEvent";
    }
}
