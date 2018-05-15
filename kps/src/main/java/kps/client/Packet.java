package kps.client;

import java.io.Serializable;

/**
 * Wrapper class for data sent on the downlink
 * @author David Phillips
 *
 */
public class Packet implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Type {
		LOG_ITEM,
		ROLE,
		BUSINESS_FIGURES,
		TRANSPORT_ROUTES,
		CUSTOMER_ROUTES,
		INFORMATION_MESSAGE,
		DESTINATIONS,
		BUSINESS_FIGURES_REQUEST,
		TRANSPORT_ROUTES_REQUEST,
		CUSTOMER_ROUTES_REQUEST,
		AUTHENTICATION_REQUEST,
		ALL_LOGS_REQUEST,
		LOG_RANGE_REQUEST,
		OK_STOP_THIS_MADNESS, /* Stop serving a client-specific log set */
		LOG_ITEM_MULTIPLE,
	}

	private Type type;
	private Object payload;

	public Packet(Type type, Object payload) {
		/* FIXME should probably sanity check payload's Java
		 * type against the packet type */
		this.type = type;
		this.payload = payload;
	}

	public Type getType() {
		return this.type;
	}

	public Object getPayload() {
		return this.payload;
	}
}
