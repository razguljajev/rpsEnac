package view.nav_display.resources;

/**
 * Generic interface for a widget whose scaling is always homothetic.
 * @author Nicolas Saporito - ENAC
 */
public interface IHomothetic {

    /**
     * Get the current scale factor
     * @return scale factor
     */
    double getScale();

    /**
     * Replace the current scale factor by a new value
     * (the zoom pivot is the origin of the local coordinates system).
     * @param scale the new scale factor
     */
    void setScale(double scale);

    /**
     * Replace the current scale factor by a new value.
     * @param scale the new scale factor
     * @param pivotX zoom pivot abscissa
     * @param pivotY zoom pivot abscissa
     */
    void setScale(double scale, double pivotX, double pivotY);

    /**
     * Apply additional scaling (multiply by the current zoom factor) 
     * (the zoom pivot is the origin of the local coordinates system).
     * @param deltaScale additional scale factor to apply
     */
    void appendScale(double deltaScale);

    /**
     * Apply additional scaling (multiply by the current zoom factor).
     * @param deltaScale additional scale factor to apply
     * @param pivotX zoom pivot abscissa
     * @param pivotY zoom pivot abscissa
     */
    void appendScale(double deltaScale, double pivotX, double pivotY);

}
