package com.geokg.pojo;

public class Link {
    private String source;
    private String name;
    private String target;
    public Link(String s, String n, String t) {
        source = s;
        name = n;
        target = t;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTarget(String target) {
        this.target = target;
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
        if (obj instanceof Link) {
            Link ls = (Link)obj;
            // source name target相同则认定为相同
            if (ls.source.equals(this.source) && ls.name.equals(this.name) && ls.target.equals(this.target)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode() * source.hashCode() * target.hashCode();
    }
}