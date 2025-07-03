package bryanthedragon.mclibreloaded.utils;

import io.netty.buffer.ByteBuf;
import bryanthedragon.mclibreloaded.network.IByteBufSerializable;

/**
 * This class is used to clock the time it takes to
 * send something between server and client to sync data like audio.
 *
 * @author Christian F. (known as Chryfi)
 */
public class LatencyTimer implements IByteBufSerializable
{
    private long startTime;
    private long endTime;

    /**
     * Saves the system time it has been created
     */
    public LatencyTimer()
    {
        this.startTime = System.currentTimeMillis();
    }


    /**
     * Sets the endTime to the current system time, if it has not been set before.
     * This method should be called after sending a packet to the server or client
     * to store the time it took to send the packet.
     */
    public void finish()
    {
        if (this.endTime == 0)
        {
            this.endTime = System.currentTimeMillis();
        }
    }

    /**
     * @param raw when true it just calculates the elapsed time,
     *            when false it takes into account the difference between server and client time from ClientHandlerTimeSync class.
     * @return the elapsed time in milliseconds since the creation of this object
     * or if this timer has finished, the elapsed time from start to end.
     */
    public long getElapsedTime()
    {
        return Math.abs((this.endTime != 0) ? (this.endTime - this.startTime) : (System.currentTimeMillis() - this.startTime));
    }

    /**
     * Reads the startTime and endTime from the given ByteBuf.
     * This method is used for deserialization of the LatencyTimer object.
     * @param buf the ByteBuf that contains the data to read from
     */
    @Override
    public void fromBytes(ByteBuf buf)
    {
        LatencyTimer timer = new LatencyTimer();

        timer.startTime = buf.readLong();
        timer.endTime = buf.readLong();
    }

    /**
     * Writes the startTime and endTime to the given ByteBuf.
     * This method is used for serialization of the LatencyTimer object.
     * @param buf the ByteBuf that the data should be written to
     */
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(this.startTime);
        buf.writeLong(this.endTime);
    }
}
