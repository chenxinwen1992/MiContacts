package org.chenxinwen.micontacts.bean;

/**
 * Created by chenxinwen on 16/8/9.11:02.
 * Email:191205292@qq.com
 */
public class Contacts {
    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 排序字母
     */
    private String sortKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }
}
