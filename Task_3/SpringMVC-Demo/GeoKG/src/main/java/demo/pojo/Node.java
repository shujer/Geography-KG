package demo.pojo;

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
}
