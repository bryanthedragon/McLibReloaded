package bryanthedragon.mclibreloaded.utils.wav;

import bryanthedragon.mclibreloaded.utils.MathUtils;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import java.nio.ByteBuffer;

public class WavePlayer
{
    private int buffer = -1;
    private int source = -1;
    private float duration;

    /**
     * Initializes the WavePlayer with the given Wave data.
     * 
     * This method generates an OpenAL buffer and source, then loads the audio data
     * from the provided Wave object into the buffer. The buffer is then attached 
     * to the source, and the source is set to be relative. The duration of the 
     * audio is also stored.
     * 
     * @param wave the Wave object containing audio data to be played
     * @return the initialized WavePlayer instance
     */
    public WavePlayer initializer(Wave wave)
    {
        this.buffer = AL10.alGenBuffers();
        ByteBuffer buffer = BufferUtils.createByteBuffer(wave.data.length);

        buffer.put(wave.data);
        buffer.flip();

        AL10.alBufferData(this.buffer, wave.getALFormat(), buffer, wave.sampleRate);

        this.duration = wave.getDuration();
        this.source = AL10.alGenSources();
        AL10.alSourcei(this.source, AL10.AL_BUFFER, this.buffer);
        AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);

        return this;
    }

    /**
     * Deletes the audio source and buffer associated with this player, freeing
     * their OpenAL resources.
     * 
     * After this method is called, the player will be in an invalid state and
     * should not be used until initialized again with {@link #initialize(Wave)}.
     */
    public void delete()
    {
        AL10.alDeleteBuffers(this.buffer);
        AL10.alDeleteSources(this.source);

        this.buffer = -1;
        this.source = -1;
    }

    /**
     * Plays the audio source if it is currently stopped or paused.
     * 
     * After this method is called, the source will be in the "playing" state.
     * 
     * @see #pause()
     * @see #stop()
     */
    public void play()
    {
        AL10.alSourcePlay(this.source);
    }

    /**
     * Pauses the audio source if it is currently playing.
     * 
     * After this method is called, the source will be in the "paused" state.
     * 
     * @see #play()
     * @see #stop()
     */
    public void pause()
    {
        AL10.alSourcePause(this.source);
    }

    /**
     * Stops the audio source if it is playing or paused.
     * 
     * After this is called, the source will be in the "stopped" state, and the
     * position will be reset to 0.
     * 
     * @see #play()
     * @see #pause()
     */
    public void stop()
    {
        AL10.alSourceStop(this.source);
    }

    /**
     * Retrieves the current state of the audio source.
     * 
     * The result will be one of the following:
     * <ul>
     * <li>{@link AL10#AL_INITIAL}</li>
     * <li>{@link AL10#AL_PLAYING}</li>
     * <li>{@link AL10#AL_PAUSED}</li>
     * <li>{@link AL10#AL_STOPPED}</li>
     * </ul>
     * 
     * @return the current state of the audio source
     */
    public int getSourceState()
    {
        return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE);
    }

    /**
     * Checks if the audio is currently playing.
     * 
     * @return true if the audio is playing, false otherwise
     */
    public boolean isPlaying()
    {
        return this.getSourceState() == AL10.AL_PLAYING;
    }

    /**
     * Checks if the audio is currently paused.
     * 
     * @return true if the audio is paused, false otherwise
     */
    public boolean isPaused()
    {
        return this.getSourceState() == AL10.AL_PAUSED;
    }

    /**
     * Retrieves whether the audio is currently stopped. This means that the audio has either been
     * stopped manually or that it has finished playing. If the audio is in the initial state,
     * it is also considered stopped.
     * 
     * @return whether the audio is currently stopped
     */
    public boolean isStopped()
    {
        int state = this.getSourceState();

        return state == AL10.AL_STOPPED || state == AL10.AL_INITIAL;
    }

    /**
     * Retrieves the current playback position of the audio in seconds.
     * 
     * @return the playback position in seconds
     */
    public float getPlaybackPosition()
    {
        return AL10.alGetSourcef(this.source, AL11.AL_SEC_OFFSET);
    }

    /**
     * This method sets the playback position to the provided seconds.
     * It clamps the seconds between 0 and the duration of the audio file.
     * @param seconds seconds to set the playback position to
     */
    public void setPlaybackPosition(float seconds)
    {
        seconds = MathUtils.clamperFloat(seconds, 0, this.duration);

        AL10.alSourcef(this.source, AL11.AL_SEC_OFFSET, seconds);
    }

    /**
     * Returns the OpenAL buffer ID associated with this WavePlayer.
     * 
     * @return the buffer ID
     */
    public int getBuffer()
    {
        return this.buffer;
    }

    /**
     * Returns the OpenAL source ID associated with this WavePlayer.
     * 
     * @return the source ID
     */
    public int getSource()
    {
        return this.source;
    }
}