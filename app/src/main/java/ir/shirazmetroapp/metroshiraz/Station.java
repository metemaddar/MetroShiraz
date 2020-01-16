package ir.shirazmetroapp.metroshiraz;

public class Station {
    private int id;
    private String name;

    public Station(){}

    public Station(String name){
        super();
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return "Station [id=" + id + ", name=" + name + "]";
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
