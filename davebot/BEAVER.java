package davebot;

import battlecode.common.*;
import java.util.Dictionary;


public class BEAVER {
    public static RobotController rc;
    public static RobotType[] canBuild = {RobotType.MINERFACTORY,
                                          RobotType.BARRACKS,
                                          RobotType.TANKFACTORY,
                                          RobotType.HELIPAD,
                                          RobotType.AEROSPACELAB,
                                          RobotType.TECHNOLOGYINSTITUTE,
                                          RobotType.TRAININGFIELD};

    public static void execute(RobotController rc_in) throws GameActionException {
        rc = rc_in;
        int executeStartRound = Clock.getRoundNum();
        if (rc.isCoreReady()) {
            RobotInfo[] enemyRobots = rc.senseNearbyRobots(24, RobotPlayer.enemyTeam);

            if (Util.flee(rc, enemyRobots)) {
                rc.yield();
                return;
            }
            if (Util.buildWithPrecedence(rc, Direction.NORTH, canBuild)) {
                rc.yield();
                return;
            }
            Util.mine(rc);
        }
        if (executeStartRound == Clock.getRoundNum()) {
            rc.yield();
        }
    }
}