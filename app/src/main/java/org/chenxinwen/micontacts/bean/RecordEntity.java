package org.chenxinwen.micontacts.bean;

/**
 * Created by chenxinwen on 16/8/9.17:07.
 * Email:191205292@qq.com
 *
 * 通话记录
 *
 */
public class RecordEntity {
    String name;
    String number;
    int type;
    String lDate;
    String duration;


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getlDate() {
        return lDate;
    }

    public void setlDate(String lDate) {
        this.lDate = lDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
