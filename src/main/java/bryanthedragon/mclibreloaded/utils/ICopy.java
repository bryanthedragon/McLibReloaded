package bryanthedragon.mclibreloaded.utils;

/**
 * The Java Cloneable interface is only a marker interface to enforce
 * a convention to override the protected Object.clone() method with a public method.
 * This does not allow for dynamic validation whether an object actually has a public clone() method.
 *
 * This interface should provide better knowledge at compile time whether a generic object
 * has a public copy method.
 * @param <T> by convention this should be the type of the class that inherits this interface
 */
public interface ICopy<T>
{
    T copier();

    /**
     * Copies the values from the specified origin to this object.
     * If the specified origin is an instance of ICopy, it calls the copier() method.
     * If not, it throws an UnsupportedOperationException.
     * @param origin the origin object to copy from
     * @throws UnsupportedOperationException if the specified origin is not an instance of ICopy
     */
    default void copier(T origin)
    { 
        if (origin instanceof ICopy)
        {
            ((ICopy<?>) origin).copier();
        }
        else
        {
            throw new UnsupportedOperationException("Cannot copy object of type " + origin.getClass().getName() + " without a public copy method.");
        }
    }
}
