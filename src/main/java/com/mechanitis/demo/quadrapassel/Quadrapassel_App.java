package com.mechanitis.demo.quadrapassel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Quadrapassel_App extends Application {
    enum Mode{
        SERVER,CLIENT
    }
    class WaitForClient implements Runnable{
        Quadrapassel_App app;
        public WaitForClient(Quadrapassel_App app){
            this.app=app;
        }
        @Override
        public void run() {
            try {
                socket=serverSocket.accept();
            } catch (IOException e) {
                return;
            }
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                return;
            }
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                return;
            }
                gameplayScene = new GameplayScene(new AnchorPane(), 500, 700, socket,app);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stage.close();
                    stage.setScene(gameplayScene);
                    stage.sizeToScene();
                    stage.show();
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            gameplayScene.Die();
                        }
                    });
                }
            });

        }
    }
    GameplayScene gameplayScene;
    ClientServerChoiceScene clientServerChoiceScene;
    ClientScene clientScene;
    ServerScene serverScene;
    WaitingForClientsScene waitingForClientsScene;
    Stage stage;
    ServerSocket serverSocket;
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    Mode mode;
    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.DECORATED);
        this.stage=stage;
        clientServerChoiceScene = new ClientServerChoiceScene(new VBox(),this);
        stage.close();
        stage.setScene(clientServerChoiceScene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }
    public void ClientInit(){
        clientScene= new ClientScene(new VBox(),this);
        stage.close();
        stage.setScene(clientScene);
        stage.sizeToScene();
        stage.show();
    }
    public void ServerInit(){
        serverScene= new ServerScene(new VBox(),this);
        stage.close();
        stage.setScene(serverScene);
        stage.sizeToScene();
        stage.show();
    }
    public void ClientMode(String hostname, int port){
        try {
            socket = new Socket(InetAddress.getByName(hostname),port);
        } catch (IOException e) {
            try {
                System.out.println("Could not establish a socket to "+InetAddress.getByName(hostname)+" "+port);
            } catch (UnknownHostException unknownHostException) {
                return;
            }
            return;
        }

        try {
             out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            return;
        }
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            return;
        }
        mode=Mode.CLIENT;
        gameplayScene = new GameplayScene(new AnchorPane(), 500, 700, socket,this);
        stage.close();
        stage.setScene(gameplayScene);
        stage.sizeToScene();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                gameplayScene.Die();
            }
        });
        stage.show();
    }
    public void ServerMode(int port){
        try {
            serverSocket = new ServerSocket(port, 0);
        } catch (IOException e) {
            return;
        }
        mode=Mode.SERVER;
        waitingForClientsScene= new WaitingForClientsScene(new VBox(),this);
        stage.close();
        stage.setScene(waitingForClientsScene);
        stage.show();
        Thread waitforclient = new Thread(new WaitForClient(this));
        waitforclient.start();
    }
    public void Die(){
        stage.close();
        try {
            socket.close();
        } catch (IOException e) {
            return;
        }
        try {
            stop();
        } catch (Exception e) {
            return;
        }
    }
    public static void main(String[] args) {
        launch();
    }


}
