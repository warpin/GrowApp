package cc.growapp.growapp.database;

public class Preferences {
    String ctrl_id;
    int version;
    int t_max;
    int t_min;
    int h_max;
    int h_min;
    int pot1_h_max;
    int pot1_h_min;
    int pot2_h_max;
    int pot2_h_min;
    int wl_max;
    int wl_min;
    int all_notify;
    int t_notify;
    int h_notify;
    int pot1_notify;
    int pot2_notify;
    int wl_notify;
    int l_notify;
    int relays_notify;
    int pumps_notify;

    // constructors
    public Preferences() {
    }

    public Preferences(String ctrl_id, int t_max, int t_min, int h_max, int h_min,
        int pot1_h_max, int pot1_h_min, int pot2_h_max, int pot2_h_min, int wl_max, int wl_min,
        int all_notify, int t_notify, int h_notify, int pot1_notify, int pot2_notify,
        int wl_notify, int l_notify, int relays_notify, int pumps_notify) {
        this.ctrl_id = ctrl_id;
        //this.version = version;
        this.t_max = t_max;
        this.t_min = t_min;
        this.h_max = h_max;
        this.h_min = h_min;
        this.pot1_h_max = pot1_h_max;
        this.pot1_h_min = pot1_h_min;
        this.pot2_h_max = pot2_h_max;
        this.pot2_h_min = pot2_h_min;
        this.wl_max = wl_max;
        this.wl_min = wl_min;

        this.all_notify = all_notify;
        this.t_notify = t_notify;
        this.h_notify = h_notify;
        this.pot1_notify = pot1_notify;
        this.pot2_notify = pot2_notify;
        this.wl_notify = wl_notify;
        this.l_notify = l_notify;
        this.relays_notify = relays_notify;
        this.pumps_notify = pumps_notify;

        //this.status = status;
    }

    public void set_preferences(String ctrl_id, int t_max, int t_min, int h_max, int h_min,
                       int pot1_h_max, int pot1_h_min, int pot2_h_max, int pot2_h_min, int wl_max, int wl_min,
                       int all_notify, int t_notify, int h_notify, int pot1_notify, int pot2_notify,
                       int wl_notify, int l_notify, int relays_notify, int pumps_notify) {
        this.ctrl_id = ctrl_id;
        //this.version = version;
        this.t_max = t_max;
        this.t_min = t_min;
        this.h_max = h_max;
        this.h_min = h_min;
        this.pot1_h_max = pot1_h_max;
        this.pot1_h_min = pot1_h_min;
        this.pot2_h_max = pot2_h_max;
        this.pot2_h_min = pot2_h_min;
        this.wl_max = wl_max;
        this.wl_min = wl_min;

        this.all_notify = all_notify;
        this.t_notify = t_notify;
        this.h_notify = h_notify;
        this.pot1_notify = pot1_notify;
        this.pot2_notify = pot2_notify;
        this.wl_notify = wl_notify;
        this.l_notify = l_notify;
        this.relays_notify = relays_notify;
        this.pumps_notify = pumps_notify;

        //this.status = status;
    }

    //setters
    public void set_ctrl_id(String ctrl_id) { this.ctrl_id=ctrl_id; }
    //public int set_version(int version) {  this.version=version; }

    public void set_t_max(int t_max) {  this.t_max=t_max; }
    public void set_t_min(int t_min) {  this.t_min=t_min; }

    public void set_h_max(int h_max) {  this.h_max=h_max; }
    public void set_h_min(int h_min) {        this.h_min=h_min;    }

    public void set_pot1_h_max(int pot1_h_max) {  this.pot1_h_max=pot1_h_max; }
    public void set_pot1_h_min(int pot1_h_min) {         this.pot1_h_min=pot1_h_min;    }
    public void set_pot2_h_max(int pot2_h_max) {  this.pot2_h_max=pot2_h_max; }
    public void set_pot2_h_min(int pot2_h_min) {         this.pot2_h_min=pot2_h_min;    }

    public void set_wl_max(int wl_max) {  this.wl_max=wl_max; }
    public void set_wl_min(int wl_min) {         this.wl_min=wl_min;    }

    public void set_all_notify(int all_notify) {  this.all_notify=all_notify; }
    public void set_t_notify(int t_notify) {  this.t_notify=t_notify; }
    public void set_h_notify(int h_notify) {  this.h_notify=h_notify; }
    public void set_pot1_notify(int pot1_notify) {  this.pot1_notify=pot1_notify; }
    public void set_pot2_notify(int pot2_notify) {  this.pot2_notify=pot2_notify; }
    public void set_l_notify(int l_notify) {  this.l_notify=l_notify; }
    public void set_wl_notify(int wl_notify) {  this.wl_notify=wl_notify; }
    public void set_relays_notify(int relays_notify) {  this.relays_notify=relays_notify; }
    public void set_pumps_notify(int pumps_notify) {  this.pumps_notify=pumps_notify; }
    
    
    // getters
    public String get_ctrl_id() { return this.ctrl_id; }
    public int get_version() { return this.version; }

    public int get_t_max() { return this.t_max; }
    public int get_t_min() { return this.t_min; }

    public int get_h_max() { return this.h_max; }
    public int get_h_min() {
        return this.h_min;
    }

    public int get_pot1_h_max() { return this.pot1_h_max; }
    public int get_pot1_h_min() {
        return this.pot1_h_min;
    }
    public int get_pot2_h_max() { return this.pot2_h_max; }
    public int get_pot2_h_min() {
        return this.pot2_h_min;
    }

    public int get_wl_max() { return this.wl_max; }
    public int get_wl_min() {
        return this.wl_min;
    }

    public int get_all_notify() { return this.all_notify; }
    public int get_t_notify() { return this.t_notify; }
    public int get_h_notify() { return this.h_notify; }
    public int get_pot1_notify() { return this.pot1_notify; }
    public int get_pot2_notify() { return this.pot2_notify; }
    public int get_l_notify() { return this.l_notify; }
    public int get_wl_notify() { return this.wl_notify; }
    public int get_relays_notify() { return this.relays_notify; }
    public int get_pumps_notify() { return this.pumps_notify; }



}
