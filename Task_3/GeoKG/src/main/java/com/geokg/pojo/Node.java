package com.geokg.pojo;

public class Node {
    private Integer category;
    private String name;
    private Integer value;
    public Node(Integer c, String n, Integer v){
        this.category = c;
        this.name = n;
        this.value = v;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    /*
     * 重写equals和hashCode，以便在Set Map中自定义判定相同的条件
     * */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Node) {
            Node no = (Node)obj;
            // name相同则认定为相同
            if (no.name.equals(this.name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
