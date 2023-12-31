package com.mattstine.dddworkshop.pizzashop.delivery;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.adapters.InProcessEventSourcedRepository;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderRef;

import java.util.*;

/**
 * @author Matt Stine
 */
final class InProcessEventSourcedDeliveryOrderRepository extends InProcessEventSourcedRepository<DeliveryOrderRef, DeliveryOrder, DeliveryOrder.OrderState, DeliveryOrderEvent, DeliveryOrderAddedEvent> implements DeliveryOrderRepository {

	Map<KitchenOrderRef , DeliveryOrderRef> kitchenOrderRefIndex = new HashMap<>();


	InProcessEventSourcedDeliveryOrderRepository(EventLog eventLog, Topic topic) {
		super(eventLog,
				DeliveryOrderRef.class,
				DeliveryOrder.class,
				DeliveryOrder.OrderState.class,
				DeliveryOrderAddedEvent.class,
				topic);

		eventLog.subscribe(topic , e -> {
			if ( e instanceof DeliveryOrderAddedEvent ) {
				DeliveryOrderAddedEvent doae = (DeliveryOrderAddedEvent) e;
				kitchenOrderRefIndex.put(doae.getState().getKitchenOrderRef(), doae.getRef());
			}
		});
	}

	@Override
	public DeliveryOrder findByKitchenOrderRef(KitchenOrderRef kitchenOrderRef)
	{
		return findByRef(kitchenOrderRefIndex.get(kitchenOrderRef));
	}
}
