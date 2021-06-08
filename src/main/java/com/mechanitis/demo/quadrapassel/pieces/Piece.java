package com.mechanitis.demo.quadrapassel.pieces;

public abstract class Piece {
    int npositions; //кол-во возможных позиций
    int current_position; //текущая позиция
    protected class Position{
        boolean[][] matrix;
        protected Position(){ //конструктор n-позиции фигуры
            matrix=new boolean[4][4];
            for(int i=0;i<4;i++)
                for (int j=0;j<4;j++)
                    matrix[i][j]=false;
        }
    }
    Position[] positions;
    protected boolean checkPosition(boolean[][] grid, int position){    //чекаем предполагаемое положение фигуры, сравниваем матрицы окружения и позиции, если они не накладываются, то живём
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                if(grid[i][j]&&positions[position].matrix[i][j])return false;
            }
        }
        return true;
    }
    public boolean[][] getCurrentPosition(){ //возвратить матрицу фигуры в данной позиции
        return positions[current_position].matrix;
    }
    public boolean checkThisPosition(boolean[][] grid){ //проверить, может ли фигура в ДАННОЙ/своей позиции занять новое окружение (для перемещения)
        return checkPosition(grid,current_position);
    }
    public boolean checkNextPosition(boolean[][] grid){ //проверить, может ли фигура в следующей позиции занять новое окружение (для вращения))
        int next_position=((current_position+1)==npositions)?0:current_position+1;
        return checkPosition(grid,next_position);
    }
    public void NextPosition(){ //принять следующую позицию
        current_position=((current_position+1)==npositions)?0:current_position+1;
    }
}
