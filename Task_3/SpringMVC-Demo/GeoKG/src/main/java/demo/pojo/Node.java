package demo.pojo;

public class Node {
    private Integer category;   // 节点所属的类别
    private String name;
    private Integer value;
    private String id;          // 若是实际节点，id是其在数据库内的IRI；若是关系节点，id是A->B中A的IRI+关系name
    private String symbol;
    public Node(Integer c, String n, Integer v, String id, String symbol){
        this.category = c;
        this.name = n;
        this.value = v;
        this.id = id;
        this.symbol = symbol;
        if (this.symbol.contains("image://")) {
            this.name = "";
        }
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public void setNodeSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNodeId(String id) {
        this.id = id;
    }

    public String getNodeSymbol() {
        return symbol;
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

    public String getNodeId() {
        return id;
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
            // id相同则认定为相同
            if (no.id.equals(this.id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
