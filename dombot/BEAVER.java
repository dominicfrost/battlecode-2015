 package dombot;

import battlecode.common.*;

public class BEAVER {
    public static void execute(RobotController rc) throws GameActionException {
        MapLocation myLocation = rc.getLocation();

        if (rc.isWeaponReady()) {
            Util.attackSomething(rc);
        }

        if (rc.isCoreReady()) {
            Direction targetDirection;
            int r = RobotPlayer.rand.nextInt(100);

            if (r > 60) {
                targetDirection = myLocation.directionTo(RobotPlayer.enemyHQLocation);
            } else if (r > 50) {
                targetDirection = myLocation.directionTo(RobotPlayer.enemyHQLocation);
            }
            Util.tryMove(rc, myLocation.directionTo(RobotPlayer.enemyHQLocation));
        }
    }
}