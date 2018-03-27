package com.example.dheeraj.college;

/**
 * Created by Dheeraj on 2/11/2018.
 */

public class listClass {

    private String title,desc,img,date;


    public listClass(){

    }
    public listClass(String title, String desc,String img,String date) {
        this.img=img;
    }


    public String getImg(){
        return img;
    }

    public void setImg(String img){
        this.img=img;
    }


}
