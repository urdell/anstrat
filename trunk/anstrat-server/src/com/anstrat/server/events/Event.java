package com.anstrat.server.events;

import com.google.common.eventbus.EventBus;

public final class Event {

	private static EventBus eventBus = new EventBus();
	
	public static void register(Object subscriber){
		eventBus.register(subscriber);
	}
	
	public static void post(Object event){
		eventBus.post(event);
	}
}
