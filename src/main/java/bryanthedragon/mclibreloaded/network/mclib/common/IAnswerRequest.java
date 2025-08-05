package bryanthedragon.mclibreloaded.network.mclib.common;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface IAnswerRequest<T extends Serializable>
{
    void setCallbackID(int callbackID);
    Optional<Integer> getCallbackID();
    
    /**
     * Get an answer packet with the provided values and callbackID.
     * The generic type of the PacketAnswer needs to equal the type of the provided value.
     * @param value
     * @return a PacketAnswer containing the value of the type of this request.
     * @throws NoSuchElementException if {@link #getCallbackID()} value is not present.
     */
    PacketAnswer<T> getAnswer(T value) throws NoSuchElementException;
}
