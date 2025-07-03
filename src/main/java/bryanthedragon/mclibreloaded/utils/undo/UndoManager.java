package bryanthedragon.mclibreloaded.utils.undo;

import java.util.LinkedList;
import java.util.List;

/**
 * Undo manager
 *
 * This class is responsible for handling undo/redo functionality. In theory,
 * it can be used to practically with any data type.
 */
public class UndoManager<T>
{
    private List<IUndo<T>> undos = new LinkedList<IUndo<T>>();
    private int position = -1;

    private int limit = 20;
    private IUndoListener<T> callback;
    private boolean simpleMerge;

    public UndoManager()
    {}

    public UndoManager(int limit)
    {
        this.limit = limit;
    }

    public UndoManager(IUndoListener<T> callback)
    {
        this.callback = callback;
    }

    public UndoManager(int limit, IUndoListener<T> callback)
    {
        this.limit = limit;
        this.callback = callback;
    }

    /**
     * Enables simple merge mode for the UndoManager.
     * 
     * When simple merge mode is enabled, only the mergeability of the present undo
     * with the new undo is checked, rather than checking both directions. This
     * simplifies the merging process.
     *
     * @return The current instance of UndoManager with simple merge enabled.
     */
    public UndoManager<T> simpleMerge()
    {
        this.simpleMerge = true;

        return this;
    }

    /**
     * Returns the current undo listener callback.
     * 
     * @return The IUndoListener instance that is used for handling undo and redo events.
     */
    public IUndoListener<T> getCallback()
    {
        return this.callback;
    }

    /**
     * Sets the undo listener callback.
     *
     * This method allows changing the current undo listener callback. The
     * callback is used to notify the caller of undo and redo events.
     *
     * @param callback The new undo listener callback.
     */
    public void setCallback(IUndoListener<T> callback)
    {
        this.callback = callback;
    }

    /* Getters */

    /**
     * Retrieves the current undo operation.
     *
     * This method returns the undo operation at the current position
     * in the undo list. If there is no valid undo operation at the
     * current position, it returns null.
     *
     * @return The current IUndo instance or null if the position is invalid.
     */
    public IUndo<T> getCurrentUndo()
    {
        if (this.position >= 0 && this.position < this.undos.size())
        {
            return this.undos.get(this.position);
        }

        return null;
    }

    /**
     * Retrieves the number of undos that have been performed.
     *
     * This method returns the total number of undos that have been executed
     * up to the current point in the undo list. It effectively returns the
     * current position in the undo list plus one, representing the count of
     * undos performed.
     *
     * @return The number of undos that have been performed.
     */
    public int getCurrentUndos()
    {
        return this.position + 1;
    }

    /**
     * Retrieves the total number of undos available in the undo list.
     *
     * This method returns the total number of undos that have been
     * added to the undo list, not including any undos that have been
     * invalidated by the user. It is the total number of undos that
     * could be redone if the user were to continue redoing undos.
     *
     * @return The total number of undos available in the undo list
     */
    public int getTotalUndos()
    {
        return this.undos.size();
    }


    /**
     * Pushes the undo into the undo list and applies it to the given context immediately.
     * This method is a convenience method that is equivalent to calling pushUndo and then calling
     * redo on the returned undo with the given context. If the undo limit is exceeded, it removes
     * the oldest undo and shifts all the other undos down by one. If a merge is possible, it removes
     * the consequent undos that could've been redone. If a merge is not possible, it increments the
     * position and adds the undo to the list.
     *
     * @param undo The undo to push and apply
     * @param context The context to apply the undo to
     * @return The undo that was just pushed or the merged undo if a merge was possible
     */
    public IUndo<T> pushApplyUndo(IUndo<T> undo, T context)
    {
        IUndo<T> newUndo = this.pushUndo(undo);

        newUndo.redo(context);

        if (this.callback != null)
        {
            this.callback.handleUndo(undo, true);
        }

        return newUndo;
    }

    /**
     * Pushes the undo into the undo list and merges it with the last
     * undo if possible. If the undo limit is exceeded, it removes the
     * oldest undo and shifts all the other undos down by one. If a
     * merge is possible, it removes the consequent undos that could've
     * been redone. If a merge is not possible, it increments the
     * position and adds the undo to the list.
     *
     * @param undo The undo to push
     * @return The undo that was just pushed or the merged undo if a
     * merge was possible
     */
    public IUndo<T> pushUndo(IUndo<T> undo)
    {
        IUndo<T> present = this.position == -1 ? null : this.undos.get(this.position);

        if (present != null && this.checkMergeability(present, undo))
        {
            this.removeConsequent();
            present.merge(undo);
        }
        else
        {
            if (this.position + 1 >= this.limit)
            {
                this.undos.remove(0);
            }
            else
            {
                this.removeConsequent();
                this.position += 1;
            }

            present = undo;
            this.undos.add(undo);
        }

        return present;
    }

    /**
     * Checks if the given present and undo are mergeable. If simple merge is
     * enabled, it only checks if the present undo is mergeable with the given
     * undo. Otherwise, it checks if both the present undo is mergeable with the
     * given undo and vice versa.
     *
     * @param present The undo that is currently present
     * @param undo The undo to check if it is mergeable with the present undo
     * @return true if the present and undo are mergeable, false otherwise
     */
    private boolean checkMergeability(IUndo<T> present, IUndo<T> undo)
    {
        if (this.simpleMerge)
        {
            return present.isMergeable(undo);
        }

        return present.isMergeable(undo) && undo.isMergeable(present);
    }

    /**
     * Removes the consequent undos that could've been redone.
     *
     * If an undo is added to the undo list, it may be possible to redo some
     * undos that were previously not redoable. This method removes those
     * consequent undos that were not redoable before the addition of the
     * new undo. This is done by removing all the undos in the undo list
     * after the current position.
     */
    protected void removeConsequent()
    {
        /* Remove the consequent undos that could've been redone */
        while (this.undos.size() > this.position + 1)
        {
            this.undos.remove(this.undos.size() - 1);
        }
    }

    /**
     * Undoes the changes done to the given context.
     *
     * This method undoes the next undo operation in the undo list
     * to the given context, effectively moving the position backward
     * by one. If there are no more undos to undo, it returns false.
     * Otherwise, it calls the undo method on the next undo and updates
     * the position. The callback is notified of the undo event if it
     * is not null.
     *
     * @param context The context to which the undo should be applied
     * @return true if the undo was successful, false if there are no more undos to undo
     */
    public boolean undo(T context)
    {
        if (this.position < 0)
        {
            return false;
        }

        IUndo<T> undo = this.undos.get(this.position);

        undo.undo(context);
        this.position -= 1;

        if (this.callback != null)
        {
            this.callback.handleUndo(undo, false);
        }

        return true;
    }


    /**
     * Redoes the changes done to the given context.
     *
     * This method re-applies the next undo operation in the undo list
     * to the given context, effectively moving the position forward
     * by one. If there are no more undos to redo, it returns false.
     * Otherwise, it calls the redo method on the next undo and updates
     * the position. The callback is notified of the redo event if it
     * is not null.
     *
     * @param context The context to which the redo should be applied
     * @return true if the redo operation was successful, false otherwise
     */
    public boolean redo(T context)
    {
        if (this.position + 1 >= this.undos.size())
        {
            return false;
        }

        IUndo<T> undo = this.undos.get(this.position + 1);

        undo.redo(context);
        this.position += 1;

        if (this.callback != null)
        {
            this.callback.handleUndo(undo, true);
        }

        return true;
    }
}