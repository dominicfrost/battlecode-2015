package sprintbot;

public class MyConstants {
    public static int ROBOT_COUNT_OFFSET = 0; // will occupy channels 0 - 20
    public static int SPAWN_TYPE_OFFSET = 21; // will occupy channels 21 - 42
    public static int ATTACK_LOCATION = 43; //will occupy channels 43 - 44
    public static int HARASS_LOCATIONS = 45; // will occupy channels 45 - 57; 
    public static int SUPPLY_REQUEST_OFFSET = 58; // will occupy channels 58 - 88, enough for 10 units to req supplies
    public static int ALONE_TOWER_INDEX = 89; //stores the index of the tower we want to send launcher to protect
}