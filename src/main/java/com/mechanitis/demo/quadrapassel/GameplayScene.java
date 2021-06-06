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
    QuadrapasselGrid grid = null;
    FuturePieceGrid fpgrid = null;
    int grid_height = 20;
    int grid_width = 14;
    Socket socket;
    int score=0;
    TextField scoretf;
    Thread funthread;
    class FunReceiver implements Runnable{
        BufferedReader in;
        @Override
        public void run() {
            if(socket==null)return;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(true){
                try {
                    String line=in.readLine();
                    if(line==null)return;
                    if(line.equals("Fun")){
                        grid.queueMove(QuadrapasselGrid.Movement.FUN);
                    }
                    if(line.equals("Win"))Win();
                } catch (IOException e) {
                    return;
                }
            }
        }
    }
    PrintWriter out;
    public void Win(){
        Die();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("You Won!");
                dialog.setContentText("Score: "+score);
                ButtonType buttonType1 = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonType1);
                dialog.showAndWait();
                app.Die();
            }
        });

    }
    public void Lose(){
        SendWin();
        Die();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("You Lost!");
                dialog.setContentText("Score: "+score);
                ButtonType buttonType1 = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonType1);
                dialog.showAndWait();
                app.Die();
            }
        });


    }
    public void SendFun(){
        if(out!=null)
        out.println("Fun");
    }
    public void SendWin(){
        if(out!=null)
        out.println("Win");
    }
    Random r =new Random();
    public void UpScore(){
        score+=r.nextInt(15);
        scoretf.setText("Score: "+ new Integer(score));
    }
    Quadrapassel_App app;
    public GameplayScene(Parent root, double width, double height, Socket socket, Quadrapassel_App app) {
        super(root, width, height);
        this.socket=socket;
        this.app=app;
        if(socket!=null)
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            return;
        }
        AnchorPane rootpane = (AnchorPane) root;
        this.fillProperty().setValue(Color.gray(0.2));
        fpgrid = new FuturePieceGrid();
        fpgrid.setHgap(5);
        fpgrid.setVgap(5);
        fpgrid.setAlignment(Pos.CENTER);
        grid = new QuadrapasselGrid(grid_width, grid_height,fpgrid, this);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);
        scoretf = new TextField();
        scoretf.setText("Score: 0");
        scoretf.setEditable(false);
        scoretf.setDisable(true);
        AnchorPane.setTopAnchor(grid, 10.0);
        AnchorPane.setLeftAnchor(grid, 10.0);
        AnchorPane.setBottomAnchor(grid, 10.0);
        rootpane.getChildren().add(grid);
        AnchorPane.setTopAnchor(fpgrid, 40.0);
        AnchorPane.setRightAnchor(fpgrid, 10.0);
        rootpane.getChildren().add(fpgrid);
        AnchorPane.setTopAnchor(scoretf, 10.0);
        AnchorPane.setRightAnchor(scoretf, 10.0);
        rootpane.getChildren().add(scoretf);
        funthread = new Thread(new FunReceiver());
        funthread.start();
        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.SPACE){
                    grid.queueMove(QuadrapasselGrid.Movement.FUN);

                }

                if(event.getCode()==KeyCode.LEFT){
                    grid.queueMove(QuadrapasselGrid.Movement.LEFT);
Random r = new Random();
                }
                if(event.getCode()==KeyCode.RIGHT){
                    grid.queueMove(QuadrapasselGrid.Movement.RIGHT);

                }
                if(event.getCode()==KeyCode.UP){
                    grid.queueMove(QuadrapasselGrid.Movement.ROTATE);


                }
                if(event.getCode()==KeyCode.DOWN) {
                    grid.queueMove(QuadrapasselGrid.Movement.DOWN);

                }
            }
        });
    }
    public void Die() {
        grid.Die();
    }

}
