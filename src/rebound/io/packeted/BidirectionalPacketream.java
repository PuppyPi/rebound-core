package rebound.io.packeted;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * "Packetreams" are just a way to use the static typing system to catch errors in semantic differences :3
 * You could use {@link InputStream}s and {@link OutputStream}s, but when writing 100 bytes in one call is different than writing two groups of 50 bytes..all of the utilities and code using streams might break everything X'D
 * 
 * Streams inherently contain the contract/expectation that breaking up a read() or write() into a group of smaller reads/writes is equivalent (not necessarily the same performance, but functionally equivalent).
 * So for packet-based systems (eg, UDP or USB or etc.), here we make a global generic API for Packet-Streams: "Packetreams"!
 * Now we can make utilities and test cases and mockups and etc. for them that don't have to worry about whether it's a network UDP/IP/etc. interface or a hardware USB interface or inter-process communication or an entirely virtual/simulation/mock implementation in-memory or etc., like we do for streams!  :D
 */
public interface BidirectionalPacketream
extends InputPacketream, OutputPacketream
{
}
