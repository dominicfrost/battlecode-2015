 package dombot;

import battlecode.common.*;

public class BEAVER {
    public static void execute(RobotController rc) throws GameActionException {
        MapLocation myLocation = rc.getLocation();

        if (rc.isWeaponReady()) {
            Util.attackSomething(rc);
        }

        if (rc.isCoreReady()) {
            Util.tryMove(rc, myLocation.directionTo(RobotPlayer.enemyHQLocation));
        }
    }
}