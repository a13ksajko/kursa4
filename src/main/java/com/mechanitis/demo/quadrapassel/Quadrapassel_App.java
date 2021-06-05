package com.mechanitis.demo.quadrapassel;

import javafx.application.Application;
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
            try {
                out.println(Integer.decode(in.readLine())+1);
            } catch (IOException e) {
                return;
            }
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
        this.stage=stage;
        gameplayScene = new GameplayScene(new AnchorPane(), 500, 700);
        clientServerChoiceScene = new ClientServerChoiceScene(new VBox(),this);
        stage.setScene(clientServerChoiceScene);
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                gameplayScene.Die();
            }
        });
    }
    public void ClientInit(){
        clientScene= new ClientScene(new VBox(),this);
        stage.setScene(clientScene);
    }
    public void ServerInit(){
        serverScene= new ServerScene(new VBox(),this);
        stage.setScene(serverScene);
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
        out.println(3);
        try {
            System.out.println(in.readLine());
        } catch (IOException e) {
            return;
        }
    }
    public void ServerMode(String hostname, int port){
        try {
            serverSocket = new ServerSocket(port, 0, InetAddress.getByName(hostname));
        } catch (IOException e) {
            return;
        }
        mode=Mode.SERVER;
        waitingForClientsScene= new WaitingForClientsScene(new VBox(),this);
        stage.setScene(waitingForClientsScene);
        Thread waitforclient = new Thread(new WaitForClient());
        waitforclient.start();
    }
    public static void main(String[] args) {
        launch();
    }


}
