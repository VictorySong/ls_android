package com.nuaakx.istest2;

/**
 * Created by viczsx on 2018/5/2.
 */

public class SatelliteInf {
    private String id;
    private float x;
    private float y;
    private lsmap Lsmap=null;
    private int r;
    private int g;
    private int b;
    private int status;
    public SatelliteInf(String t_id,float t_x,float t_y,int t_r,int t_g,int t_b,int t_status){
        this.id = t_id;
        this.x  = t_x;
        this.y  = t_y;
        r = t_r;
        g = t_g;
        b = t_b;
        status = t_status;
    }

    public SatelliteInf(SatelliteInf tem){
        this.id = tem.getid();
        this.x = tem.getx();
        this.y = tem.gety();
        this.Lsmap = tem.getLsmap();
        this.r = tem.getr();
        this.g = tem.getg();
        this.b = tem.getb();
    }

    public String getid(){
        return id;
    }

    public float getx(){
        return x;
    }

    public float gety(){
        return y;
    }

    public int getr(){
        return r;
    }

    public int getg() {
        return g;
    }

    public int getb(){
        return b;
    }

    public void setid(String t_id){
        id = t_id;
    }

    public void setx(float t_x){
        x = t_x;
    }

    public void sety(float t_y){
        y = t_y;
    }
    public lsmap getLsmap(){
        return Lsmap;
    }
    public void setLsmap(lsmap t_Lsmap){
        Lsmap = t_Lsmap;
    }
    public int getStatus(){return status;}
    public void setStatus(int t_status){
        status = t_status;
    }
}
