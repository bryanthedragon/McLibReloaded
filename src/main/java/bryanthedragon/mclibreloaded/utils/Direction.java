package bryanthedragon.mclibreloaded.utils;

public enum Direction
{
    TOP(0.5F, 0F), LEFT(0F, 0.5F), BOTTOM(0.5F, 1F), RIGHT(1F, 0.5F);

    public final float anchorX;
    public final float anchorY;
    public final int factorX;
    public final int factorY;

    private Direction(float anchorX, float anchorY)
    {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.factorX = (int) Interpolations.lerp(-1, 1, anchorX);
        this.factorY = (int) Interpolations.lerp(-1, 1, anchorY);
    }

    /**
     * Checks if the direction is horizontal.
     *
     * @return true if the direction is LEFT or RIGHT, false otherwise.
     */
    public boolean isHorizontal()
    {
        return this == LEFT || this == RIGHT;
    }

    /**
     * Checks if the direction is vertical.
     *
     * @return true if the direction is TOP or BOTTOM, false otherwise.
     */
    public boolean isVertical()
    {
        return this == TOP || this == BOTTOM;
    }

    /**
     * Returns the opposite direction of the current one.
     *
     * Examples:
     * <ul>
     * <li>{@link #TOP} -> {@link #BOTTOM}</li>
     * <li>{@link #BOTTOM} -> {@link #TOP}</li>
     * <li>{@link #LEFT} -> {@link #RIGHT}</li>
     * <li>{@link #RIGHT} -> {@link #LEFT}</li>
     * </ul>
     *
     * @return the opposite direction of the current one.
     */
    public Direction opposite()
    {
        if (this == TOP)
        {
            return BOTTOM;
        }
        else if (this == BOTTOM)
        {
            return TOP;
        }
        else if (this == LEFT)
        {
            return  RIGHT;
        }

        /* this == RIGHT */
        return LEFT;
    }
}
