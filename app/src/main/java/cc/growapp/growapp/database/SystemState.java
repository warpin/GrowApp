package cc.growapp.growapp.database;

public class SystemState {
    String ctrl_id;
    int light_state;
    int t;
    int h;
    int pot1_h;
    int pot2_h;
    int relay1_state;
    int relay2_state;
    int pump1_state;
    int pump2_state;
    int water_level;
    String date;

    // constructors
    public SystemState() {
    }

    /*public SystemState(int ctrl_id) {
    }*/

    public SystemState(String ctrl_id, int light_state, int t, int h, int pot1_h, int pot2_h,
                       int relay1_state, int relay2_state, int pump1_state, int pump2_state,
                       int water_level, String date) {
        this.ctrl_id = ctrl_id;
        this.light_state = light_state;
        this.t = t;
        this.h = h;
        this.pot1_h = pot1_h;
        this.pot2_h = pot2_h;
        this.relay1_state = relay1_state;
        this.relay2_state = relay2_state;
        this.pump1_state = pump1_state;
        this.pump2_state = pump2_state;
        this.water_level = water_level;
        this.date = date;

    }

    // setters
    public void set_ctrl_id(String ctrl_id) {
        this.ctrl_id = ctrl_id;
    }
    public void set_light_state(int light_state) {  this.light_state=light_state; }
    public void set_t(int t) {  this.t=t; }
    public void set_h(int h) {  this.h=h; }
    public void set_pot1_h(int pot1_h) {  this.pot1_h=pot1_h; }
    public void set_pot2_h(int pot2_h) {  this.pot2_h=pot2_h; }
    public void set_relay1_state(int relay1_state) {  this.relay1_state=relay1_state; }
    public void set_relay2_state(int relay2_state) {  this.relay2_state=relay2_state; }
    public void set_pump1_state(int pump1_state) {  this.pump1_state=pump1_state; }
    public void set_pump2_state(int pump2_state) {  this.pump2_state=pump2_state; }
    public void set_water_level(int water_level) {  this.water_level=water_level; }
    public void set_date(String date) {  this.date=date; }

    // getters
    public String get_ctrl_id() { return this.ctrl_id; }
    public int get_light_state() { return this.light_state; }
    public int get_t() { return this.t; }
    public int get_h() { return this.h; }
    public int get_pot1_h() { return this.pot1_h; }
    public int get_pot2_h() { return this.pot2_h; }
    public int get_relay1_state() { return this.relay1_state; }
    public int get_relay2_state() { return this.relay2_state; }
    public int get_pump1_state() { return this.pump1_state; }
    public int get_pump2_state() { return this.pump2_state; }
    public int get_water_level() { return this.water_level; }
    public String get_date() { return this.date; }

}
