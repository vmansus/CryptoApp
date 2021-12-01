package com.example.myapplication.DB;

public class Properties {
    private int ID = -1;
    private String Name;
    private String type;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString(){
        String result = "";
        result += "ID：" + this.ID + "，";
        result += "字段名：" + this.Name + "，";
        result += "类型：" + this.type + "，";

        return result;
    }

}
