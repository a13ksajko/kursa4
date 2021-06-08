package com.mechanitis.demo.quadrapassel;
//класс самого приложения
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

public class Quadrapassel_App extends Application{
    class WaitForClient implements Runnable{
        Quadrapassel_App app;   //app - ссылка на приложение
        public WaitForClient(Quadrapassel_App app){
            this.app=app;
        }   //конструктор
        @Override
        public void run() { //создаёт геймплей-сцену в режиме сервера
            try {
                socket=serverSocket.accept();   //получаем соединение клиента с сервером
            } catch (IOException e) {
                return;
            }
            gameplayScene = new GameplayScene(new AnchorPane(), 500, 700, socket, app);   //создаём новую сцену игры, передавая в неё полученное соединение и ссылку на приложение
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stage.close();  //закрывает текующее окно (waiting for client)
                    stage.setScene(gameplayScene);  //устанавливаем новую сцену
                    stage.sizeToScene();    //перерасчитываем размеры будущего окна
                    stage.show();   //показываем сцену (gameplayScene)
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {//на запрос о закрытии запускаем die
                        @Override
                        public void handle(WindowEvent event) {
                            gameplayScene.Die();
                        }
                    });
                }
            });

        }
    }
    GameplayScene gameplayScene;    //окно с игровым полем
    ClientServerChoiceScene clientServerChoiceScene;    //первая сцена
    ClientScene clientScene;    //сцена клиента
    ServerScene serverScene;    //сцена сервера
    WaitingForClientsScene waitingForClientsScene;  //сцена ожидания подключения со стороны сервера
    Stage stage;    //дефолтное окно
    ServerSocket serverSocket;  //спец сокет, который будет ждать подключения, после чего станет обычным сокетом
    Socket socket=null; //просто сокет
    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.DECORATED);
        this.stage=stage;
        clientServerChoiceScene = new ClientServerChoiceScene(new VBox(),this); //описываем объявление сцены главного меню с корневым элементом vbox
        //stage.close(); //зочем
        stage.setScene(clientServerChoiceScene);    //устанавливаем сцену
        stage.setResizable(false);  //запрещаем разрабатывать размер
        stage.sizeToScene();    //пересчёт размеров стэйджа под размер сцены
        stage.show();   //показать сцену
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
    public void ClientMode(String hostname, int port){//функция подключения клиента к серверу
        if(port!=0){    //port=0 при одиночной игре
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
        }
        gameplayScene = new GameplayScene(new AnchorPane(), 500, 700, socket,this);
        stage.close();
        stage.setScene(gameplayScene);
        stage.sizeToScene();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {//если закрыли окно, то закрыть все потоки
            @Override
            public void handle(WindowEvent event) {
                gameplayScene.Die();
            }
        });
        stage.show();
    }
    public void ServerMode(int port){
        try {//будучи серваком ожидать прибытия на сервак клиентов
            serverSocket = new ServerSocket(port, 0);
        } catch (IOException e) {
            return;
        }
        waitingForClientsScene = new WaitingForClientsScene(new VBox(),this);
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
