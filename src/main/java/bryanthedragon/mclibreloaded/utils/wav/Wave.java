package bryanthedragon.mclibreloaded.utils.wav;

import org.lwjgl.openal.AL10;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Wave
{
    public int audioFormat;
    public int numChannels;
    public int sampleRate;
    public int byteRate;
    public int blockAlign;
    public int bitsPerSample;
    public byte[] data;

    public Wave(int audioFormat, int numChannels, int sampleRate, int byteRate, int blockAlign, int bitsPerSample, byte[] data)
    {
        this.audioFormat = audioFormat;
        this.numChannels = numChannels;
        this.sampleRate = sampleRate;
        this.byteRate = byteRate;
        this.blockAlign = blockAlign;
        this.bitsPerSample = bitsPerSample;
        this.data = data;
    }

    /**
     * Returns the number of bytes per sample based on the bits per sample.
     * 
     * @return the number of bytes per sample
     */
    public int getBytesPerSample()
    {
        return this.bitsPerSample / 8;
    }

    /**
     * Returns the duration of the audio data in seconds.
     * 
     * @return the duration of the audio data in seconds
     */
    public float getDuration()
    {
        return this.data.length / this.numChannels / this.getBytesPerSample() / (float) this.sampleRate;
    }

    /**
     * Returns the OpenAL format of the audio data.
     * 
     * The possible formats are:
     * <ul>
     * <li>AL_FORMAT_STEREO8</li>
     * <li>AL_FORMAT_MONO8</li>
     * <li>AL_FORMAT_STEREO16</li>
     * <li>AL_FORMAT_MONO16</li>
     * </ul>
     * 
     * @return the OpenAL format of the audio data
     */
    public int getALFormat()
    {
        int bytes = this.getBytesPerSample();
        if (bytes == 1)
        {
            if (this.numChannels == 2)
            {
                return AL10.AL_FORMAT_STEREO8;
            }
            else if (this.numChannels == 1)
            {
                return AL10.AL_FORMAT_MONO8;
            }
        }
        else if (bytes == 2)
        {
            if (this.numChannels == 2)
            {
                return AL10.AL_FORMAT_STEREO16;
            }
            else if (this.numChannels == 1)
            {
                return AL10.AL_FORMAT_MONO16;
            }
        }
        throw new IllegalStateException("Current WAV file has unusual configuration... channels: " + this.numChannels + ", BPS: " + bytes);
    }

    /**
     * Returns the number of bytes to scan in the audio data to go from one
     * pixel to the next at the given pixels per second rate.
     * 
     * @param pixelsPerSecond the number of pixels per second
     * @return the number of bytes to scan
     */
    public int getScanRegion(float pixelsPerSecond)
    {
        return (int) (this.sampleRate / pixelsPerSecond) * this.getBytesPerSample() * this.numChannels;
    }

    /**
     * Converts the audio data to 16-bit signed PCM format.
     * 
     * @return the converted audio data
     */
    public Wave convertTo16()
    {
        final int bytes = 16 / 8;
        int c = this.data.length / this.numChannels / this.getBytesPerSample();
        int byteRate = c * this.numChannels * bytes ;
        byte[] data = new byte[byteRate];
        boolean isFloat = this.getBytesPerSample() == 4;
        Wave wave = new Wave(this.audioFormat, this.numChannels, this.sampleRate, byteRate, bytes * this.numChannels, 16, data);
        ByteBuffer sample = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());
        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(data.length).order(ByteOrder.nativeOrder());
        for (int i = 0; i < c * this.numChannels; i++)
        {
            sample.clear();
            for (int j = 0; j < this.getBytesPerSample(); j++)
            {
                sample.put(this.data[i * this.getBytesPerSample() + j]);
            }
            if (isFloat)
            {
                sample.flip();
                dataBuffer.putShort((short) (sample.getFloat() * 0xffff / 2));
            }
            else
            {
                sample.put((byte) 0);
                sample.flip();
                dataBuffer.putShort((short) ((int) (sample.getInt() / (0xffffff / 2F) * (0xffff / 2F))));
            }
        }
        dataBuffer.flip();
        dataBuffer.get(data);
        return wave;
    }
}