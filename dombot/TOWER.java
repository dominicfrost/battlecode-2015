package dombot;

import battlecode.common.*;

public class TOWER {
    public static void execute(RobotController rc) throws GameActionException {
        if (rc.isWeaponReady()) {
            Util.attackSomething(rc);
        }
    }
}