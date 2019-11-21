package rebound.io.packeted.sio;

import static java.util.Objects.*;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import rebound.exceptions.ClosedIOException;
import rebound.io.packeted.BidirectionalPacketream;

public class NetworkDatagramPacketream
implements BidirectionalPacketream, Closeable
{
	protected final DatagramSocket socket;
	
	public NetworkDatagramPacketream(DatagramSocket socket)
	{
		this.socket = requireNonNull(socket);
	}
	
	
	
	@Override
	public void close() throws IOException
	{
		socket.close();
	}
	
	
	
	@Override
	public int receive(byte[] array, int offset, int length) throws IOException, EOFException, ClosedIOException
	{
		//Allows short reads
		DatagramPacket p = new DatagramPacket(array, offset, length);
		socket.receive(p);
		return p.getLength();
	}
	
	@Override
	public int send(byte[] array, int offset, int length) throws IOException, EOFException, ClosedIOException
	{
		//Does not allow short writes
		socket.send(new DatagramPacket(array, offset, length));
		return length;
	}
}
