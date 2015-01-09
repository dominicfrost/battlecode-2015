package dombot;

import battlecode.common.*;

public class TRAININGFIELD {

    public static int cost = 100;

    public static void execute(RobotController rc) throws GameActionException {
        if (rc.isCoreReady() && rc.getTeamOre() >= cost &&
                rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + RobotType.COMMANDER.ordinal()) != 1) {
            cost *= 2;
            Util.trySpawn(rc, Direction.NORTH, RobotType.COMMANDER);
        }
    }
}