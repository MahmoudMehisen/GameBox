package com.example.mina.gamebox;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;

public class Card extends android.support.v7.widget.AppCompatImageButton {

    private int pictureID, playPosition , finishedPosition , number , inPlayPosition;
    private String name;
    private Boolean isPlay , isFinished;

    public Card(Context context) {
        super(context);
    }

    public Card(Context context , int pictureID, ViewGroup.LayoutParams layoutParams) {
        super(context);
        this.pictureID = pictureID;
        setImageResource(pictureID);

        name = context.getResources().getResourceName(pictureID);

        if(Character.isDigit(name.codePointAt(name.length()-2))){
            number = name.charAt(name.length()-2) - '0';
            number*=10;
            number += name.charAt(name.length()-1) - '0';
        }
        else{
            number = name.charAt(name.length()-1) - '0';
        }

        if(name.contains("clubs")) {
            name = "clubs";
        }
        else if(name.contains("diamonds")) {
            name = "diamonds";
        }
        else if(name.contains("hearts")) {
            name = "hearts";
        }
        else if(name.contains("spades")) {
            name = "spades";
        }

        Log.i("min" , name + " " + Integer.toString(number));

        playPosition = finishedPosition = 0;
        isPlay = isFinished = false;

        ViewGroup.LayoutParams newLayout = new ViewGroup.LayoutParams(layoutParams);
        setLayoutParams(newLayout);
    }

    public int getinPlayPosition() {
        return inPlayPosition;
    }

    public int getInPlayPosition() {
        return inPlayPosition;
    }

    public void setInPlayPosition(int inPlayPosition) {
        this.inPlayPosition = inPlayPosition;
    }

    public void setinPlayPosition(int inPlayPosition) {
        this.inPlayPosition = inPlayPosition;
    }

    public int getPictureID() {
        return pictureID;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public int getPlayPosition() {
        return playPosition;
    }

    public int getFinishedPosition() {
        return finishedPosition;
    }

    public Boolean getPlay() {
        return isPlay;
    }

    public void setPlayPosition(int playPosition) {
        this.playPosition = playPosition;
    }

    public void setFinishedPosition(int finishedPosition) {
        this.finishedPosition = finishedPosition;
    }

    public void setPlay(Boolean play) {
        isPlay = play;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setPosition(Float x , Float y)
    {
        setX(x);
        setY(y);
    }

    public Pair<Float , Float> getPosition()
    {
        return  new Pair<Float,Float>(getX() , getY());
    }

    public void setPosition(Pair<Float, Float> position)
    {
        setX(position.first);
        setY(position.second);
    }
}
