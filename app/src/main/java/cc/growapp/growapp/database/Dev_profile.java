package cc.growapp.growapp.database;

public class Dev_profile {
    String ctrl_id;
    int light_control;
    int t_control;
    int h_control;
    int pot1_control;
    int pot2_control;
    int relay1_control;
    int relay2_control;
    int pump1_control;
    int pump2_control;
    int water_control;
    int auto_watering1;
    int auto_watering2;


    // constructors
    public Dev_profile() {        
    }
    // constructors
    public Dev_profile(int ctrl_id) {
    }

    public Dev_profile(String ctrl_id, int light_control, int t_control, int h_control, int pot1_control,
            int pot2_control, int relay1_control, int relay2_control, int pump1_control, int pump2_control,
            int water_control, int auto_watering1,int auto_watering2) {
        this.ctrl_id = ctrl_id;
        this.light_control = light_control;
        this.t_control = t_control;
        this.h_control = h_control;
        this.pot1_control = pot1_control;
        this.pot2_control = pot2_control;
        this.relay1_control = relay1_control;
        this.relay2_control = relay2_control;
        this.pump1_control = pump1_control;
        this.pump2_control = pump2_control;
        this.water_control = water_control;
        this.auto_watering1 = auto_watering1;
        this.auto_watering2 = auto_watering2;

    }

    // setters
    public void set_ctrl_id(String ctrl_id) { this.ctrl_id=ctrl_id; }
    public void set_light_control(int light_control) { this.light_control=light_control; }
    public void set_t_control(int t_control) { this.t_control=t_control; }
    public void set_h_control(int h_control) {  this.h_control=h_control; }
    public void set_pot1_control(int pot1_control) {  this.pot1_control=pot1_control; }
    public void set_pot2_control(int pot2_control) {  this.pot2_control=pot2_control; }
    public void set_relay1_control(int relay1_control) {  this.relay1_control=relay1_control; }
    public void set_relay2_control(int relay2_control) {  this.relay2_control=relay2_control; }
    public void set_pump1_control(int pump1_control) {  this.pump1_control=pump1_control; }
    public void set_pump2_control(int pump2_control) {  this.pump2_control=pump2_control; }
    public void set_water_control(int water_control) {  this.water_control=water_control; }
    public void set_auto_watering1(int auto_watering1) {  this.auto_watering1=auto_watering1; }
    public void set_auto_watering2(int auto_watering2) {  this.auto_watering2=auto_watering2; }

    // getters
    public String get_ctrl_id() { return this.ctrl_id; }
    public int get_light_control() { return this.light_control; }
    public int get_t_control() { return this.t_control; }
    public int get_h_control() { return this.h_control; }
    public int get_pot1_control() { return this.pot1_control; }
    public int get_pot2_control() { return this.pot2_control; }
    public int get_relay1_control() { return this.relay1_control; }
    public int get_relay2_control() { return this.relay2_control; }
    public int get_pump1_control() { return this.pump1_control; }
    public int get_pump2_control() { return this.pump2_control; }
    public int get_water_control() { return this.water_control; }
    public int get_auto_watering1() { return this.auto_watering1; }
    public int get_auto_watering2() { return this.auto_watering2; }

}
