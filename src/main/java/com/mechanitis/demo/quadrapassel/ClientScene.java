package com.mechanitis.demo.quadrapassel;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ClientScene extends Scene {
    TextField ServerHostnameDesc = new TextField();
    TextField ServerPortDesc = new TextField();
    TextField ServerHostname = new TextField();
    TextField ServerPort = new TextField();
    Button connect = new Button();
    double width;
    double height;
    Quadrapassel_App app;
    public ClientScene(Parent root, Quadrapassel_App app) {
        super(root);
        this.app=app;
        ServerHostnameDesc.setText("Enter hostname:");
        ServerHostnameDesc.setEditable(false);
        ServerPortDesc.setText("Enter port:");
        ServerPortDesc.setEditable(false);
        connect.setText("Connect");
        VBox rootpane = (VBox) root;
        rootpane.getChildren().add(ServerHostnameDesc);
        rootpane.getChildren().add(ServerHostname);
        rootpane.getChildren().add(ServerPortDesc);
        rootpane.getChildren().add(ServerPort);
        rootpane.getChildren().add(connect);
        connect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                app.ClientMode(ServerHostname.getText(),Integer.decode(ServerPort.getText()));
            }
        });
    }
}
