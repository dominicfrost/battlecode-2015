package sprintbot;

public class MyConstants {
    public static int ROBOT_COUNT_OFFSET = 0; // will occupy channels 0 - 20
    public static int SPAWN_TYPE_OFFSET = 21; // will occupy channels 21 - 42
    public static int ATTACK_LOCATION = 43; //will occupy channels 43 - 44 
    public static int ALONE_TOWER_INDEX = 66; //stores the index of the tower we want to send launcher to protect

    public static int TOWER_UNDER_DISTRESS = 67; //occupies 67-72 (6, 1 for each tower)
    public static int TOWER_UNDER_DISTRESS_LOCATION = 73; // 73 - 84 (12, 1 for each towers x and y position)
    public static int TARGET_TOWER_X = 85; // 85
    public static int TARGET_TOWER_Y= 86; // 86
    public static int NUM_POINTS_OF_INTEREST_OFFSET = 60000; // will occupy channel 60000;
    public static int POINTS_OF_INTEREST_OFFSET = 60001; // will occupy channels 60,001 - ?;
}