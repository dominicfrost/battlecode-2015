package dombot;

import battlecode.common.*;

public class TANKFACTORY {
    public static void execute(RobotController rc) throws GameActionException {
        if (rc.isCoreReady() && rc.getTeamOre() >= 250 &&
                rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + RobotType.TANK.ordinal()) < 7) {
            Util.trySpawn(rc, Direction.NORTH, RobotType.TANK);
        }
    }
}