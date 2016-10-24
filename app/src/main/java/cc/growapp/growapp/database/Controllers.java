package cc.growapp.growapp.database;

public class Controllers {
    String ctrl_id;
    String name;
    String an;
    //int status;
    //String created_at;

    // constructors
    public Controllers() {
    }

    /*public Controllers(String name) {
        this.name = name;
        //this.status = status;
    }*/

    public Controllers(String ctrl_id, String name, String an) {
        this.ctrl_id = ctrl_id;
        this.name = name;
        this.an = an;
        //this.status = status;
    }

    public Controllers(String ctrl_id, String name) {
        this.ctrl_id = ctrl_id;
        this.name = name;
    }

    // setters
    public void set_ctrl_id(String ctrl_id) {
        this.ctrl_id = ctrl_id;
    }
    public void set_name(String name) {
        this.name = name;
    }
    public void set_an(String an) {
        this.an = an;
    }

    // getters
    public String get_ctrl_id() {
        return this.ctrl_id;
    }
    public String get_name() {
        return this.name;
    }
    public String get_an() {
        return this.an;
    }

}
