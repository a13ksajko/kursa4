package com.mechanitis.demo.quadrapassel;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class WaitingForClientsScene extends Scene {
    public WaitingForClientsScene(Parent root, Quadrapassel_App app) {
        super(root);
        VBox rootpane = (VBox) root;
        TextField field = new TextField();
        field.setText("Waiting for client... ");
        rootpane.getChildren().add(field);
    }
}
