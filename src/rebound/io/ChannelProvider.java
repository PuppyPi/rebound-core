/*
 * Created on Jun 19, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.io;

import java.io.IOException;
import java.nio.channels.Channel;

/**
 * Say you've got a thing that needs a channel, but instead of ACTUALLY OPENING THE CHANNEL
 * you just want to pass a thing *which can be used to open the channel*,
 * ie, containing all your configurationthings, and ready to actually connect/open it when all
 * the heavy stuff goes down! (XD)
 * 
 * @author RProgrammer
 */
public interface ChannelProvider<C extends Channel>
{
	public C open() throws IOException;
}
