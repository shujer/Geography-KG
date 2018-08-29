package demo.pojo;

public class Link {
    private String source;
    private String name;
    private String target;
    public Link(String s, String p, String t) {
        source = s;
        target = p;
        name = t;
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
}
