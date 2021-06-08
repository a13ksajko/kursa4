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
        super(root);    //вызываем конструктор родителя
        VBox rootpane = (VBox) root;    //переприсваеваем корневой элемент
        //this.fillProperty().setValue(Color.WHITE);  //зочем
        sp = new Button();  //кнопка single player
        sp.setMinSize(200,100); //размеры кнопки
        sp.setText("Single Player");    //надпись
        client = new Button();
        client.setMinSize(200,100);
        client.setText("Client");
        server= new Button();
        server.setMinSize(200,100);
        server.setText("Server");
        rootpane.getChildren().add(client);     //добавляем кнопки к корневому элементу
        rootpane.getChildren().add(server);
        rootpane.getChildren().add(sp);
        client.setOnAction(new EventHandler<ActionEvent>() {    //когда нажатие происходит вызывается handle ...
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
