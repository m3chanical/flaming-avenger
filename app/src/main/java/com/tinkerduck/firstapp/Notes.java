package com.tinkerduck.firstapp;

import java.io.Serializable;

/**
 * Created by m3chanical on 1/20/15.
 */
public class Notes implements Serializable {
    private int id;
    private String title;
    private String noteBody;
    private String noteType;

    public Notes(){}

    public Notes(String title, String noteBody, String noteType){
        super();
        this.title = title;
        this.noteBody = noteBody;
        this.noteType = noteType;

    }

    @Override
    public String toString(){
        return "Notes [id=" + id + ", title=" + title + ", noteBody=" + noteBody +
                ", noteType=" + noteType + "]";
    }

    public void setId(int id){
        this.id = id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setBody(String body){
        this.noteBody = body;
    }

    public void setType(String type){
        this.noteType = type;
    }

    //get id, title, body, type
    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getNoteBody(){
        return noteBody;
    }

    public String getNoteType(){
        return noteType;
    }


}
