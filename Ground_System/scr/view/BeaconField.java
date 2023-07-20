package view;

import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.BeaconData;

/**
 * This class regroups every component we need to display a beacon on the beaconList
 */
public class BeaconField extends GridPane {

    public BeaconField(BeaconData bd) {
        // Create UI elements for the beacon entry
        Label beaconCodeLabel = new Label(bd.getBeacon().getCode());
        Label timeLabel = new Label(bd.getTime());
        Label distanceLabel = new Label("");

        Button directToButton = new Button("Direct To");
        directToButton.setWrapText(true);
        directToButton.setMaxWidth(Double.MAX_VALUE);

        Bindings.bindBidirectional(distanceLabel.textProperty(), bd.getDistanceNM());

        // Modify the different label to correspond to Airbus Color code
        beaconCodeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        timeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        distanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        timeLabel.setTextFill(Color.GREEN);
        distanceLabel.setTextFill(Color.GREEN);

        // Modify the beacon entry components properties
        getStyleClass().add("beacon-entry");
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(10));
        setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        // Set the column and row constraints
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        ColumnConstraints col3 = new ColumnConstraints();
        col1.setPercentWidth(25);
        col2.setPercentWidth(50);
        col3.setPercentWidth(25);
        col2.setHalignment(HPos.CENTER);
        col3.setHalignment(HPos.RIGHT);
        getColumnConstraints().addAll(col1, col2, col3);

        GridPane.setFillWidth(beaconCodeLabel, true);
        GridPane.setFillWidth(timeLabel, true);
        GridPane.setFillWidth(directToButton, true);

        RowConstraints row1 = new RowConstraints();
        getRowConstraints().add(row1);

        // Set the beacon name in the upper left corner
        add(beaconCodeLabel, 0, 0);

        // Set the time in the upper right corner
        add(timeLabel, 2, 0);

        add(distanceLabel, 1, 0);

        // Set the anchor constraints for the button
        AnchorPane.setRightAnchor(directToButton, 0.0);
        AnchorPane.setBottomAnchor(directToButton, 0.0);

        // Remove the button row and update the remaining row constraints
        getChildren().remove(directToButton);
        setFillWidth(distanceLabel, true);
        setColumnSpan(distanceLabel, 3);
    }
}
