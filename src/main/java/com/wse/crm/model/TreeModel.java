package com.wse.crm.model;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/24 0024 20:09
 */
public class TreeModel {

    private Integer id;
    private Integer pId;
    private String name;
    private Boolean checked=false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
