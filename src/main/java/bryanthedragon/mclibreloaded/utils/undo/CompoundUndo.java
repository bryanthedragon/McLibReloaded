package bryanthedragon.mclibreloaded.utils.undo;

import java.util.ArrayList;
import java.util.List;

/**
 * Compound undo
 *
 * This generalized undo element allows to undo/redo multiple undo/redos
 * at a time
 */
public class CompoundUndo <T> implements IUndo<T>
{
    private List<IUndo<T>> undos = new ArrayList<IUndo<T>>();
    private boolean mergable = true;

    @SuppressWarnings("unchecked")
    public CompoundUndo(IUndo<T>... undos)
    {
        for (IUndo<T> undo : undos)
        {
            if (undo == null)
            {
                continue;
            }

            this.undos.add(undo);
        }
    }

    /**
     * Get all the undos in this compound undo
     *
     * @return All undos in this compound undo
     */
    public List<IUndo<T>> getUndos()
    {
        return this.undos;
    }

    /**
     * Get first undo matching given class
     *
     * @param clazz The class to look for
     * @return The first undo matching the given class, or null if none
     */
    public IUndo<T> getFirst(Class<? extends IUndo<T>> clazz)
    {
        int i = 0;

        while (i < this.undos.size())
        {
            IUndo<T> undo = this.undos.get(i);

            if (clazz.isAssignableFrom(undo.getClass()))
            {
                return undo;
            }

            i += 1;
        }

        return null;
    }

    /**
     * Get last undo matching given class
     *
     * @param clazz The class to look for
     * @return The last undo matching the given class, or null if none
     */
    public IUndo<T> getLast(Class<? extends IUndo<T>> clazz)
    {
        int i = this.undos.size() - 1;

        while (i >= 0)
        {
            IUndo<T> undo = this.undos.get(i);

            if (clazz.isAssignableFrom(undo.getClass()))
            {
                return undo;
            }

            i -= 1;
        }

        return null;
    }

    /**
     * Check if this compound undo has at least one undo of the given class
     *
     * @param clazz The class to look for
     * @return true if this compound undo contains at least one undo of the given class, false otherwise
     */
    public boolean has(Class<? extends IUndo<T>> clazz)
    {
        for (IUndo<T> undo : this.undos)
        {
            if (clazz.isAssignableFrom(undo.getClass()))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Mark this compound undo as unmergable.
     *
     * @return This compound undo instance after marking as unmergable.
     */
    @Override
    public IUndo<T> noMerging()
    {
        this.mergable = false;

        return this;
    }

    /**
     * Check if this compound undo is mergeable with given undo
     *
     * Two compound undos are mergeable if they contain the same number of undos, and each
     * undo in the first compound undo is mergeable with the corresponding undo in the
     * second compound undo. If this compound undo has been marked as unmergable, then
     * this method always returns false.
     *
     * @param undo The compound undo to check
     * @return true if this compound undo is mergeable with given undo, false otherwise
     */
    @Override
    public boolean isMergeable(IUndo<T> undo)
    {
        if (this.mergable && undo instanceof CompoundUndo && ((CompoundUndo<T>) undo).undos.size() == this.undos.size())
        {
            CompoundUndo<T> compound = (CompoundUndo<T>) undo;

            for (int i = 0; i < this.undos.size(); i++)
            {
                if (!this.undos.get(i).isMergeable(compound.undos.get(i)))
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Merge the current compound undo with the given compound undo.
     *
     * This method takes each undo operation in the current compound undo
     * and merges it with the corresponding undo operation in the given compound undo,
     * provided they are mergeable.
     *
     * @param undo The compound undo to merge with
     */
    @Override
    public void merge(IUndo<T> undo)
    {
        CompoundUndo<T> theUndo = (CompoundUndo<T>) undo;

        for (int i = 0, c = this.undos.size(); i < c; i++)
        {
            IUndo<T> otherChildUndo = theUndo.undos.get(i);
            IUndo<T> myUndo = this.undos.get(i);

            if (myUndo.isMergeable(otherChildUndo))
            {
                myUndo.merge(otherChildUndo);
            }
        }
    }

    /**
     * Undo the changes done to the given context in the reverse order
     * in which they were added to this compound undo.
     *
     * @param context The context to which the undos should be applied
     */
    @Override
    public void undo(T context)
    {
        for (int i = this.undos.size() - 1; i >= 0; i--)
        {
            this.undos.get(i).undo(context);
        }
    }

    /**
     * Redo the changes done to the given context in the order in which
     * they were added to this compound undo.
     *
     * @param context The context to which the redos should be applied
     */
    @Override
    public void redo(T context)
    {
        for (IUndo<T> undo : this.undos)
        {
            undo.redo(context);
        }
    }
}