package view.nav_display.map;

import fr.enac.sita.visuradar.data.param.VisualParameters;
import fr.enac.sita.visuradar.model.IZone;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import model.SelfTraffic;
import view.nav_display.resources.TranslatableHomotheticPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Pane containing the map provided. In this project, it represents the France Map. The map is built thanks to multiple points and their coordinates
 */
public class MapPane extends TranslatableHomotheticPane {

    public MapPane(SelfTraffic selfTraffic){
        layoutXProperty().bind(selfTraffic.getPosition().xProperty().negate());
        layoutYProperty().bind(selfTraffic.getPosition().yProperty().negate());

        Rotate rot = new Rotate();
        rot.pivotXProperty().bind(selfTraffic.getPosition().xProperty());
        rot.pivotYProperty().bind(selfTraffic.getPosition().yProperty());
        rot.angleProperty().bind(selfTraffic.getHeading().negate());
        getTransforms().add(rot);

    }

    public void setMap(List<IZone> zones, VisualParameters visualParameters){
        List<Polygon> shape = new ArrayList<>();
        for(IZone z : zones) {
            Polygon p = new Polygon();
            p.getPoints().addAll(z.getVertexesXYArray());
            p.setFill(visualParameters.getBaseMapFillColor());
            p.setStroke(visualParameters.getBaseMapStrokeColor());
            p.setStrokeWidth(visualParameters.getBaseMapStrokeWidth());
            shape.add(p);
        }
        for(Polygon p : shape) getChildren().add(p);
    }
}