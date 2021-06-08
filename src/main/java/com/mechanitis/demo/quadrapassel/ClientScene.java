package com.mechanitis.demo.quadrapassel;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ClientScene extends Scene {
    TextField ServerHostnameDesc = new TextField(); //описание
    TextField ServerPortDesc = new TextField();
    TextField ServerHostname = new TextField(); //поле ввода
    TextField ServerPort = new TextField();
    Button connect = new Button();
    Quadrapassel_App app;
    public ClientScene(Parent root, Quadrapassel_App app) {
        super(root);    //вызываем конструктор родителя
        this.app=app;   //переприсваеваем приложение
        ServerHostnameDesc.setText("Enter the server's hostname:"); //ставим текст
        ServerHostnameDesc.setEditable(false); //запрещаем редактирование
        ServerPortDesc.setText("Enter the server's port:");
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
                app.ClientMode(ServerHostname.getText(),Integer.decode(ServerPort.getText()));      //запускаем клиент с тем, что было введено в поля ввода
            }
        });
    }
}
