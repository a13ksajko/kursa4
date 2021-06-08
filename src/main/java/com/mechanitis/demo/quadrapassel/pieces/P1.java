package com.mechanitis.demo.quadrapassel.pieces;

public class P1 extends Piece{
    public P1(){
        npositions=1;
        positions=new Position[npositions];
        for(int i=0;i<npositions;i++)
            positions[i]=new Position();    //присваиваем первой позиции массив??
        current_position=0;
        positions[0].matrix[0][0]=true;
        positions[0].matrix[0][1]=true;
        positions[0].matrix[1][0]=true;
        positions[0].matrix[1][1]=true;
    }
}
