package com.mechanitis.demo.quadrapassel;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ServerScene extends Scene {
    TextField ServerHostnameDesc = new TextField();
    TextField ServerHostname = new TextField();
    TextField ServerPortDesc = new TextField();
    TextField ServerPort = new TextField();
    Button connect = new Button();
    Quadrapassel_App app;
    public ServerScene(Parent root,Quadrapassel_App app) {
        super(root);
        this.app=app;
        ServerHostnameDesc.setText("IP Address to bind to:");
        ServerHostnameDesc.setEditable(false);
        ServerPortDesc.setText("Port to listen on:");
        ServerPortDesc.setEditable(false);
        connect.setText("Host");
        connect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                 app.ServerMode(ServerHostname.getText(),Integer.decode(ServerPort.getText()));
            }
        });
        VBox rootpane = (VBox) root;
        rootpane.getChildren().add(ServerHostnameDesc);
        rootpane.getChildren().add(ServerHostname);
        rootpane.getChildren().add(ServerPortDesc);
        rootpane.getChildren().add(ServerPort);
        rootpane.getChildren().add(connect);
    }
}
