package kps.server;

import javax.annotation.ParametersAreNonnullByDefault;

import kps.util.MailPriority;
@ParametersAreNonnullByDefault
public class Mail {
	public final Destination to;
	public final Destination from;
	public final MailPriority priority;
	public final double weight;
	public final double volume;
	
	public Mail(Destination to, Destination from, MailPriority priority, double weight, double volume) {
		super();
		this.to = to;
		this.from = from;
		this.priority = priority;
		this.weight = weight;
		this.volume = volume;
	}
}
