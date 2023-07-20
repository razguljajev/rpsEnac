package model;

import javafx.beans.property.SimpleStringProperty;
import model.enumeration.Status;
import model.enumeration.TypeOfOrder;

import java.util.Arrays;

/**
 * An order is created each time we perform a new order. First, it is created with the Process Status
 * It is change when we receive a Report Message
 * @see Controller.MessageControllerGround
 * @see view.SceneController
 */
public class Order {
    private final TypeOfOrder typeOfOrder;
    private final String[] parameters;
    private Status status;
    private final String message;
    private final SimpleStringProperty orderMessage;

    public Order(TypeOfOrder typeOfOrder, String ... parameters){
        this.typeOfOrder = typeOfOrder;
        this.parameters = parameters;
        status = Status.IN_PROCESS;
        message = "Process";
        orderMessage = new SimpleStringProperty();
        setOrderMessage();
    }

    public TypeOfOrder getTypeOfOrder() {
        return typeOfOrder;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setOrderMessage(){
        orderMessage.set("[" + status.name() + "] " + typeOfOrder.name() + " : " + message + " " + Arrays.toString(parameters));
    }

    public SimpleStringProperty orderMessageProperty() {
        return orderMessage;
    }

    @Override
    public String toString() {
        return "[" + status.name() + "] " + typeOfOrder.name() + " : " + message + " " + Arrays.toString(parameters);
    }
}
