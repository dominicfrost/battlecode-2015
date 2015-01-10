package dombot;

import battlecode.common.*;

public class BARRACKS {
    public static void execute(RobotController rc) throws GameActionException {
        if (rc.isCoreReady() && rc.getTeamOre() >= 60) {

            if (rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + RobotType.SOLDIER.ordinal()) < 3) {
                Util.trySpawn(rc, Direction.NORTH, RobotType.SOLDIER);
            } else if (rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + RobotType.TANK.ordinal()) > 5 &&
                    rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + RobotType.SOLDIER.ordinal()) < 10) {
                Util.trySpawn(rc, Direction.NORTH, RobotType.SOLDIER);
            }
        }
    }
}