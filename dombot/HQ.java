package dombot;
import globals.*;

import battlecode.common.*;

public class HQ {
    public static RobotController rc;

    public static void execute(RobotController rcIn) throws GameActionException {
        rc = rcIn;
        int[] numRobots = countAndBroadcastRobotTypes();

        if (rc.isWeaponReady()) {
            Util.attackSomething(rc, RobotPlayer.myRange, RobotPlayer.enemyTeam);
        }

        if (rc.isCoreReady() && rc.getTeamOre() >= 100) {
            if (numRobots[RobotType.BEAVER.ordinal()] < 5) {
                Util.trySpawn(rc, RobotPlayer.myHQLocation.directionTo(RobotPlayer.enemyHQLocation), RobotType.BEAVER);
            }
        }
    }

    public static int[] countAndBroadcastRobotTypes() throws GameActionException {
        RobotInfo[] myRobots = rc.senseNearbyRobots(9999999, RobotPlayer.myTeam);
        int[] typeCount = new int[21];
        for (RobotInfo r : myRobots) {
            RobotType rt = r.type;
            switch (rt) {
                case AEROSPACELAB:
                    typeCount[RobotType.AEROSPACELAB.ordinal()]++;
                    break;
                case BARRACKS:
                    typeCount[RobotType.BARRACKS.ordinal()]++;
                    break;
                case BASHER:
                    typeCount[RobotType.BASHER.ordinal()]++;
                    break;
                case BEAVER:
                    typeCount[RobotType.BEAVER.ordinal()]++;
                    break;
                case COMMANDER:
                    typeCount[RobotType.COMMANDER.ordinal()]++;
                    break;
                case COMPUTER:
                    typeCount[RobotType.COMPUTER.ordinal()]++;
                    break;
                case DRONE:
                    typeCount[RobotType.DRONE.ordinal()]++;
                    break;
                case HANDWASHSTATION:
                    typeCount[RobotType.HANDWASHSTATION.ordinal()]++;
                    break;
                case HELIPAD:
                    typeCount[RobotType.HELIPAD.ordinal()]++;
                    break;
                case HQ:
                    typeCount[RobotType.HQ.ordinal()]++;
                    break;
                case LAUNCHER:
                    typeCount[RobotType.LAUNCHER.ordinal()]++;
                    break;
                case MINER:
                    typeCount[RobotType.MINER.ordinal()]++;
                    break;
                case MINERFACTORY:
                    typeCount[RobotType.MINERFACTORY.ordinal()]++;
                    break;
                case MISSILE:
                    typeCount[RobotType.MISSILE.ordinal()]++;
                    break;
                case SOLDIER:
                    typeCount[RobotType.SOLDIER.ordinal()]++;
                    break;
                case SUPPLYDEPOT:
                    typeCount[RobotType.SUPPLYDEPOT.ordinal()]++;
                    break;
                case TANK:
                    typeCount[RobotType.TANK.ordinal()]++;
                    break;
                case TANKFACTORY:
                    typeCount[RobotType.TANKFACTORY.ordinal()]++;
                    break;
                case TECHNOLOGYINSTITUTE:
                    typeCount[RobotType.TECHNOLOGYINSTITUTE.ordinal()]++;
                    break;
                case TOWER:
                    typeCount[RobotType.TOWER.ordinal()]++;
                    break;
                case TRAININGFIELD:
                    typeCount[RobotType.TRAININGFIELD.ordinal()]++;
                    break;
            }
        }

        for (int i = 0; i < typeCount.length; i++) {
            rc.broadcast(MyConstants.ROBOT_COUNT_OFFSET + i, typeCount[i]);
        }

        return typeCount;
        }
}