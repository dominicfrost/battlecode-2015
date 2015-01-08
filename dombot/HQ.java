package dombot;

import battlecode.common.*;

public class HQ {
    public static void execute(RobotController rc) throws GameActionException {
        if (rc.isWeaponReady()) {
            Util.attackSomething(rc);
        }

        if (rc.isCoreReady() && rc.getTeamOre() >= 100) {
            trySpawn(rc, RobotPlayer.myHQLocation.directionTo(RobotPlayer.enemyHQLocation), RobotType.BEAVER);
        }
    }

    // This method will attempt to spawn in the given direction (or as close to it as possible)
    static void trySpawn(RobotController rc, Direction d, RobotType type) throws GameActionException {
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2,3,-3,4};
        int dirint = Util.directionToInt(d);
        boolean blocked = false;
        while (offsetIndex < 8 && !rc.canSpawn(Util.directions[(dirint+offsets[offsetIndex]+8)%8], type)) {
            offsetIndex++;
        }
        if (offsetIndex < 8) {
            rc.spawn(Util.directions[(dirint+offsets[offsetIndex]+8)%8], type);
        }
    }
}