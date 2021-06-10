package com.mechanitis.demo.quadrapassel.pieces;

public abstract class Piece {
    int npositions;
    int current_position;
    int num_fig;
    protected static class Position{
        boolean[][] matrix;
        protected Position(){
            matrix=new boolean[4][4];
            for(int i=0;i<4;i++)
                for (int j=0;j<4;j++)
                    matrix[i][j]=false;
        }
    }
    Position[] positions;
    protected boolean checkPosition(boolean[][] grid, int position){
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                if(grid[i][j]&&positions[position].matrix[i][j])return false;
            }
        }
        return true;
    }
    public int check_num(){ return num_fig; }
    public boolean[][] getCurrentPosition(){ return positions[current_position].matrix; }
    public boolean checkThisPosition(boolean[][] grid){ return checkPosition(grid,current_position); }
    public boolean checkNextPosition(boolean[][] grid){
        int next_position=((current_position+1)==npositions)?0:current_position+1;
        return checkPosition(grid,next_position);
    }
    public void NextPosition(){ current_position=((current_position+1)==npositions)?0:current_position+1; }
}
