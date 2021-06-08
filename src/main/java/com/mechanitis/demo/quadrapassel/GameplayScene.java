package com.mechanitis.demo.quadrapassel;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class GameplayScene extends Scene {
    QuadrapasselGrid grid = null;   //графическая решётка - игровое поле
    FuturePieceGrid fpgrid = null;  //следующая фигура
    int grid_height = 20;   //размер решётки в квадратах
    int grid_width = 14;
    Socket socket;  //сокет для общения с соперником
    int score = 0;  //а не очко обычно губит
    TextField scoretf;  //текстовое поле, выводящее кол-во очков
    Thread funthread;   //поток, считывающий входящие сообщения из сокета
    PrintWriter out;    //поток, пищущий в сокет
    Random r = new Random();    //ГСЧ
    Quadrapassel_App app;   //описываем переменную-ссылку на объект-приложение

    public GameplayScene(Parent root, double width, double height, Socket socket, Quadrapassel_App app) {
        super(root, width, height); //вызываем конструктор родителя
        this.socket = socket;   //переприсваеваем сокет и приложение
        this.app = app;
        if (socket != null) //если игра на 2, то создаём поток вывода в сокет
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                return;
            }
        AnchorPane rootpane = (AnchorPane) root;    //переприсваеваем корневой элемент
        //this.fillProperty().setValue(Color.gray(0.2)); //заливка ебаная
        fpgrid = new FuturePieceGrid(); //создаём окно(хуету) для будущего элемента
        fpgrid.setHgap(5);  //гор/вертик отступ между полями в решётке будущего элемента
        fpgrid.setVgap(5);
        fpgrid.setAlignment(Pos.CENTER);    //выравнить по центру
        grid = new QuadrapasselGrid(grid_width, grid_height, fpgrid, this);     //создаём основное поле
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);
        scoretf = new TextField();  //поле счёта очков
        scoretf.setText("Score: 0");
        scoretf.setEditable(false);
        scoretf.setDisable(true);   //делаем неактивным
        AnchorPane.setTopAnchor(grid, 10.0);    //отступы от краёв окна
        AnchorPane.setLeftAnchor(grid, 10.0);
        AnchorPane.setBottomAnchor(grid, 10.0);
        rootpane.getChildren().add(grid);
        AnchorPane.setTopAnchor(fpgrid, 40.0);
        AnchorPane.setRightAnchor(fpgrid, 10.0);
        rootpane.getChildren().add(fpgrid);
        AnchorPane.setTopAnchor(scoretf, 10.0);
        AnchorPane.setRightAnchor(scoretf, 10.0);
        rootpane.getChildren().add(scoretf);
        funthread = new Thread(new FunReceiver());  //поток считывания команд с сокета
        funthread.start();  //стартуем
        this.setOnKeyPressed(new EventHandler<KeyEvent>() { //регаем нажатие клавиш
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode() == KeyCode.LEFT) {
                    grid.queueMove(QuadrapasselGrid.Movement.LEFT);
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    grid.queueMove(QuadrapasselGrid.Movement.RIGHT);
                }
                if (event.getCode() == KeyCode.UP) {
                    grid.queueMove(QuadrapasselGrid.Movement.ROTATE);
                }
                if (event.getCode() == KeyCode.DOWN) {
                    grid.queueMove(QuadrapasselGrid.Movement.DOWN);
                }
            }
        });
    }

    public void Win() {//диалоговое окно "вы победили"
        Die();
        Platform.runLater(new Runnable() {//всё, что обрабатывается и выводится на экран - делается одним потоком, поэтому мы создаём запускаемый объект и кладём в очередь главного потока
            @Override
            public void run() {
                Dialog<String> dialog = new Dialog<>();//чекнуть класс Dialog
                dialog.setTitle("You Won!");
                dialog.setContentText("Score: " + score);
                ButtonType buttonType1 = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonType1);   //добавляем кнопку
                dialog.showAndWait();   //показываем окно и жд1м, пока его не закроют
                app.Die();  //гг
            }
        });

    }

    public void Lose() {
        SendWin();
        Die();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("You Lost!");
                dialog.setContentText("Score: " + score);
                ButtonType buttonType1 = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonType1);
                dialog.showAndWait();
                app.Die();
            }
        });
    }

    public void SendFun() {
        if (out != null)
            out.println("Fun");
    }

    public void SendWin() {
        if (out != null)
            out.println("Win");
    }

    public void UpScore() {
        score += r.nextInt(15);
        scoretf.setText("Score: " + (score));
    }

    public void Die() {
        grid.Die();
    }

    class FunReceiver implements Runnable {
        BufferedReader in;

        @Override
        public void run() {
            if (socket == null) return; //если сети пизда или игра на одного, выходим
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));    //пытаемся создать поток ввода из сокета
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {  //в бесконечном цикле читаем сообщения из сокета
                try {
                    String line = in.readLine();
                    if (line == null) return; //если противник сгорел нахуй и ливнул из жизни (закрыл окно), мы считаем нулл из сокета
                    if (line.equals("Fun")) {
                        grid.queueMove(QuadrapasselGrid.Movement.FUN);  //если противник выбил линию, то он пересылает нам сообщение "фан", и мы добавляем строку снизу в наше игровое поле
                    }
                    if (line.equals("Win")) Win();  //если противник проигралвин, то изи победка
                } catch (IOException e) {
                    return;
                }
            }
        }
    }

}
