package dombot;

import battlecode.common.*;

public class HQ {
    public static RobotController rc;

    public static void execute(RobotController rcIn) throws GameActionException {
        rc = rcIn;
        int[] numRobots = countAndBroadcastRobotTypes();

        if (rc.isWeaponReady()) {
            Util.attackSomething(rc);
        }

        if (rc.isCoreReady() && rc.getTeamOre() >= 100) {
            if (numRobots[BEAVER.ordinal()] < 5) {
                trySpawn(RobotPlayer.myHQLocation.directionTo(RobotPlayer.enemyHQLocation), RobotType.BEAVER);
            }
        }
    }

    // This method will attempt to spawn in the given direction (or as close to it as possible)
    static void trySpawn(Direction d, RobotType type) throws GameActionException {
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

    public static int[] countAndBroadcastRobotTypes() throws GameActionException {
        RobotInfo[] myRobots = rc.senseNearbyRobots(9999999, RobotPlayer.myTeam);
        int[] typeCount = new int[21];
        for (RobotInfo r : myRobots) {
            switch (r.type) {
                case AEROSPACELAB:
                    typeCount[AEROSPACELAB.ordinal()]++;
                    break;
                case BARRACKS:
                    typeCount[BARRACKS.ordinal()]++;
                    break;
                case BASHER:
                    typeCount[BASHER.ordinal()]++;
                    break;
                case BEAVER:
                    typeCount[BEAVER.ordinal()]++;
                    break;
                case COMMANDER:
                    typeCount[COMMANDER.ordinal()]++;
                    break;
                case COMPUTER:
                    typeCount[COMPUTER.ordinal()]++;
                    break;
                case DRONE:
                    typeCount[DRONE.ordinal()]++;
                    break;
                case HANDWASHSTATION:
                    typeCount[HANDWASHSTATION.ordinal()]++;
                    break;
                case HELIPAD:
                    typeCount[HELIPAD.ordinal()]++;
                    break;
                case HQ:
                    typeCount[HQ.ordinal()]++;
                    break;
                case LAUNCHER:
                    typeCount[LAUNCHER.ordinal()]++;
                    break;
                case MINER:
                    typeCount[MINER.ordinal()]++;
                    break;
                case MINERFACTORY:
                    typeCount[MINERFACTORY.ordinal()]++;
                    break;
                case MISSILE:
                    typeCount[MISSILE.ordinal()]++;
                    break;
                case SOLDIER:
                    typeCount[SOLDIER.ordinal()]++;
                    break;
                case SUPPLYDEPOT:
                    typeCount[SUPPLYDEPOT.ordinal()]++;
                    break;
                case TANK:
                    typeCount[TANK.ordinal()]++;
                    break;
                case TANKFACTORY:
                    typeCount[TANKFACTORY.ordinal()]++;
                    break;
                case TECHNOLOGYINSTITUTE:
                    typeCount[TECHNOLOGYINSTITUTE.ordinal()]++;
                    break;
                case TOWER:
                    typeCount[TOWER.ordinal()]++;
                    break;
                case TRAININGFIELD:
                    typeCount[TRAININGFIELD.ordinal()]++;
                    break;
            }
        }

        for (int i = 0; i < typeCount.length; i++) {
            rc.brodcast(MyConstants.ROBOT_COUNT_OFFSET + i, typeCount[i]);
        }

        return typeCount;
    }
}