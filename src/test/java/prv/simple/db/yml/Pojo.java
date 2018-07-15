package prv.simple.db.yml;

public class Pojo {
    public String name;

    public Pojo() {
        this.name = "defualt";
    }

    public Pojo(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "name->" + name;
    }
}