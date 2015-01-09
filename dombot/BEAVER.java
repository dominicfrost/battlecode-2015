package dombot;

import battlecode.common.*;

public class BEAVER {
    public static void execute(RobotController rc) throws GameActionException {
        MapLocation myLocation = rc.getLocation();

        if (rc.isWeaponReady()) {
            Util.attackSomething(rc, RobotPlayer.myRange, RobotPlayer.enemyTeam);
        }

        if (rc.isCoreReady()) {
            Direction targetDirection;
            int r = RobotPlayer.rand.nextInt(100);

            if (r > 60) {
                Util.tryMove(rc, myLocation.directionTo(RobotPlayer.enemyHQLocation));
            } else if (r > 50) {
                Util.tryMove(rc, myLocation.directionTo(RobotPlayer.enemyHQLocation).rotateRight());
            } else if (r > 40) {
                Util.tryMove(rc, myLocation.directionTo(RobotPlayer.enemyHQLocation).rotateRight().rotateRight());
            } else if (r > 30) {
                Util.tryMove(rc, myLocation.directionTo(RobotPlayer.enemyHQLocation).rotateLeft());
            } else if (r > 20) {
                Util.tryMove(rc, myLocation.directionTo(RobotPlayer.enemyHQLocation).rotateLeft().rotateLeft());
            } else {
                rc.mine();
            }
        }
    }
}