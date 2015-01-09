package davebot;

import battlecode.common.*;
import java.util.Dictionary;


public class BEAVER {

    public static void execute(RobotController rc) throws GameActionException {
        if (rc.isCoreReady()) {
            RobotInfo[] enemyRobots = rc.senseNearbyRobots(24, RobotPlayer.enemyTeam);

            if (utils.flee(rc, enemyRobots)) {
                return;
            }
//            if (build(rc, allyTypeCount)) {
//                return;
//            }
            mine(rc);
        }
    }



    /*
     * miner factory
     * 2 barracks
     * tank factory
     * helipad
     * aerospace lab
     * technology instutue
     * training field
     */
    public static boolean build(RobotController rc, int[] allyTypeCount) throws GameActionException{

        if (allyTypeCount[RobotType.MINERFACTORY.ordinal()] < 1) {
            return tryBuild(rc, RobotType.MINERFACTORY);
        }
        if (allyTypeCount[RobotType.BARRACKS.ordinal()] < 2) {
            return tryBuild(rc, RobotType.BARRACKS);
        }
        if (allyTypeCount[RobotType.TANKFACTORY.ordinal()] < 1) {
            return tryBuild(rc, RobotType.TANKFACTORY);
        }
        if (allyTypeCount[RobotType.HELIPAD.ordinal()] < 1) {
            return tryBuild(rc, RobotType.HELIPAD);
        }
        if (allyTypeCount[RobotType.AEROSPACELAB.ordinal()] < 1) {
            return tryBuild(rc, RobotType.AEROSPACELAB);
        }
        if (allyTypeCount[RobotType.TECHNOLOGYINSTITUTE.ordinal()] < 1) {
            return tryBuild(rc, RobotType.TECHNOLOGYINSTITUTE);
        }
        if (allyTypeCount[RobotType.TRAININGFIELD.ordinal()] < 1) {
            return tryBuild(rc, RobotType.TRAININGFIELD);
        }
        return false;
    }

    public static void mine(RobotController rc) throws GameActionException {
        rc.mine();
    }

    public static boolean tryBuild(RobotController rc, RobotType type) throws GameActionException {
        double ore = rc.getTeamOre();
        if (ore >= type.oreCost) {
            findAndBuild(rc, type);
            return true;
        }

        return false;
    }

    public static void findAndBuild(RobotController rc, RobotType type) throws GameActionException {
        Direction[] dirs = Direction.values();
        for (Direction dir: dirs) {
            if (rc.canBuild(dir, type)) {
                rc.build(dir, type);
                return;
            }
        }
    }
}