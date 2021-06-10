package com.mechanitis.demo.quadrapassel;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ClientServerChoiceScene extends Scene {
    Button client, server, sp;
    public ClientServerChoiceScene(Parent root, Quadrapassel_App app) {
        super(root);
        VBox rootpane = (VBox) root;
        sp = new Button();
        sp.setMinSize(200,100);
        sp.setText("Single Player");
        client = new Button();
        client.setMinSize(200,100);
        client.setText("Client");
        server= new Button();
        server.setMinSize(200,100);
        server.setText("Server");
        rootpane.getChildren().add(client);
        rootpane.getChildren().add(server);
        rootpane.getChildren().add(sp);
        client.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                app.ClientInit();
            }
        });
        sp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) { app.ClientMode("no",0);
            }
        });
        server.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                app.ServerInit();
            }
        });
    }
}
