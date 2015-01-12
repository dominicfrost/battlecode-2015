package sprintbot;

import battlecode.common.*;

import java.util.*;

public class Util {
    public static Random rand = new Random();
    public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};


    /*
     * spawns the current spawn type if possible
     */
    public static boolean buildWithPrecedence(RobotController rc, Direction d, RobotType[] canBuild) throws GameActionException{
        if (!rc.isCoreReady()) {
            return false;
        }

        int numToBuild;
        double myOre = rc.getTeamOre();

        for (RobotType type: canBuild) {
            numToBuild = rc.readBroadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal());
            if (numToBuild > 0 && myOre >= type.oreCost) {
                tryBuild(rc, d, type);
                numToBuild--;
                rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal(), numToBuild);
                return true;
            }
        }
        return false;
    }

    /*
     * spawns the current spawn type if possible
     */
    public static boolean spawnWithPrecedence(RobotController rc, Direction d, RobotType[] canSpawn) throws GameActionException{
        if (!rc.isCoreReady()) {
            return false;
        }

        int numToSpawn;
        double myOre = rc.getTeamOre();

        for (RobotType type: canSpawn) {
            numToSpawn = rc.readBroadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal());
            if (numToSpawn > 0 && myOre >= type.oreCost) {
                trySpawn(rc, d, type);
                numToSpawn--;
                rc.broadcast(MyConstants.SPAWN_TYPE_OFFSET + type.ordinal(), numToSpawn);
                return true;
            }
        }
        return false;
    }

    public static void generalAttack(RobotController rc) throws GameActionException{
        MapLocation goal = new MapLocation(rc.readBroadcast(MyConstants.ATTACK_LOCATION), rc.readBroadcast(MyConstants.ATTACK_LOCATION + 1));
        System.out.println("GOAL: " + goal.toString());
        if (rc.isCoreReady()) {
            if (rc.canAttackLocation(goal)) {
                System.out.println("can attack goal!: " + goal.toString());
                rc.attackLocation(goal);
                return;
            }
            RobotInfo[] enemyRobots = rc.senseNearbyRobots(999999, RobotPlayer.enemyTeam);
            if (!attack(rc, enemyRobots)) {
                if (rc.getHealth() < 40) {
                    System.out.println("my health or supply is too low, going to hq");
                    tryMove(rc, rc.getLocation().directionTo(rc.senseHQLocation()));
                    return;
                }
                System.out.println("moving to goal!: " + goal.toString());
                tryMove(rc, rc.getLocation().directionTo(goal));
            }
        }
    }

    public static int mapLocToInt(MapLocation m){
        return (m.x*10000 + m.y);
    }

    public static MapLocation intToMapLoc(int i){
        return new MapLocation(i/10000,i%10000);
    }

    // This method will attempt to move in Direction d (or as close to it as possible)
    public static void tryMove(RobotController rc, Direction d) throws GameActionException {
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2};
        int dirint = directionToInt(d);
        boolean blocked = false;
        while (offsetIndex < 5 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
            offsetIndex++;
        }
        if (offsetIndex < 5) {
            rc.move(directions[(dirint+offsets[offsetIndex]+8)%8]);
        }
    }

    // This method will attack an enemy in sight, if there is one
    public static void attackSomething(RobotController rc, int myRange, Team enemyTeam) throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
        if (enemies.length > 0) {
            rc.attackLocation(enemies[0].location);
        }
    }

    public static boolean saftToMoveTo(RobotController rc, MapLocation myLocation,  MapLocation[] enemyTowers, MapLocation enemyHq) {
        if (myLocation.distanceSquaredTo(enemyHq) <= 35) {
            return false;
        }

        for (MapLocation tower: enemyTowers) {
            if (myLocation.distanceSquaredTo(tower) <= 24) {
                return false;
            }
        }

        return true;
    }

    // This method will attempt to build in the given direction (or as close to it as possible)
    public static void tryBuild(RobotController rc, Direction d, RobotType type) throws GameActionException {
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2,3,-3,4};
        int dirint = directionToInt(d);
        boolean blocked = false;
        while (offsetIndex < 8 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
            offsetIndex++;
        }
        if (offsetIndex < 8) {
            rc.build(directions[(dirint+offsets[offsetIndex]+8)%8], type);
        }
    }
    
    //use this to return the map location in a specific direction from your robot
    public static MapLocation getAdjacentLocation(RobotController rc, Direction dir, MapLocation loc){
    	return (loc.add(dir));
    }
    
    //mine while preferring areas w/ greater ore, but stay within 1/2 distance to enemy hq
    public static void SmartMine(RobotController rc) throws GameActionException {
        if (!rc.isCoreReady()) {
            return;
        }

        MapLocation myLocation = rc.getLocation();
        double oreCount = rc.senseOre(myLocation);
        
        double ore_N = rc.senseOre(getAdjacentLocation(rc, Direction.NORTH, myLocation));
        double ore_NE = rc.senseOre(getAdjacentLocation(rc, Direction.NORTH_EAST, myLocation));
        double ore_E = rc.senseOre(getAdjacentLocation(rc, Direction.EAST, myLocation));
        double ore_SE = rc.senseOre(getAdjacentLocation(rc, Direction.SOUTH_EAST, myLocation));
        double ore_S = rc.senseOre(getAdjacentLocation(rc, Direction.SOUTH, myLocation));
        double ore_SW = rc.senseOre(getAdjacentLocation(rc, Direction.SOUTH_WEST, myLocation));
        double ore_W = rc.senseOre(getAdjacentLocation(rc, Direction.WEST, myLocation));
        double ore_NW = rc.senseOre(getAdjacentLocation(rc, Direction.NORTH_WEST, myLocation));
        
        if(oreCount < ore_N)
        	tryMove(rc, Direction.NORTH);
        else if(oreCount < ore_NE)
        	tryMove(rc, Direction.NORTH_EAST);
        else if(oreCount < ore_E)
        	tryMove(rc, Direction.EAST);
        else if(oreCount < ore_SE)
        	tryMove(rc, Direction.SOUTH_EAST);
        else if(oreCount < ore_S)
        	tryMove(rc, Direction.SOUTH);
        else if(oreCount < ore_SW)
        	tryMove(rc, Direction.SOUTH_WEST);
        else if(oreCount < ore_W)
        	tryMove(rc, Direction.WEST);
        else if(oreCount < ore_NW)
        	tryMove(rc, Direction.NORTH_WEST);
        else if(oreCount > 0) {
            rc.mine();
        }else {
            int fate = rand.nextInt(8);
            Util.tryMove(rc, Util.intToDirection(fate));
        }
    }

    // mine like a dummy
    public static void mine(RobotController rc) throws GameActionException {
        if (!rc.isCoreReady()) {
            return;
        }

        MapLocation myLocation = rc.getLocation();
        double oreCount = rc.senseOre(myLocation);
        if (oreCount > 0) {
            rc.mine();
        }else {
            int fate = rand.nextInt(8);
            Util.tryMove(rc, Util.intToDirection(fate));
        }
    }

    public static Direction intToDirection(int i) {
        switch(i) {
            case 0:
                return Direction.NORTH;
            case 1:
                return Direction.NORTH_EAST;
            case 2:
                return Direction.EAST;
            case 3:
                return Direction.SOUTH_EAST;
            case 4:
                return Direction.SOUTH;
            case 5:
                return Direction.SOUTH_WEST;
            case 6:
                return Direction.WEST;
            case 7:
                return Direction.NORTH_WEST;
            default:
                return Direction.NORTH;
        }
    }

    public static int directionToInt(Direction d) {
        switch(d) {
            case NORTH:
                return 0;
            case NORTH_EAST:
                return 1;
            case EAST:
                return 2;
            case SOUTH_EAST:
                return 3;
            case SOUTH:
                return 4;
            case SOUTH_WEST:
                return 5;
            case WEST:
                return 6;
            case NORTH_WEST:
                return 7;
            default:
                return 0;
        }
    }

    public static boolean flee(RobotController rc, RobotInfo[] enemyRobots) throws GameActionException{
        ArrayDeque<MapLocation> enemieLocs = new ArrayDeque<MapLocation>();
        for (RobotInfo robot: enemyRobots) {
            if (robot.type.attackRadiusSquared <= robot.location.distanceSquaredTo(rc.getLocation())) {
                enemieLocs.add(robot.location);
            }
        }
        if (enemieLocs.size() > 0) {
            int counterx = 0;
            int countery = 0;
            for (MapLocation oppLoc : enemieLocs) {
                counterx += oppLoc.x;
                countery += oppLoc.y;
            }

            MapLocation avg = new MapLocation(counterx / enemieLocs.size(), countery / enemieLocs.size());
            Direction dirToGoal = rc.getLocation().directionTo(avg).opposite();
            tryMove(rc, dirToGoal);
            return true;
        }

        return false;
    }

    public static int[] getRobotCount(RobotController rc) throws GameActionException{
        int[] robotCount = new int[21];
        for (int i = 0; i < robotCount.length; i++) {
            robotCount[i] = rc.readBroadcast(MyConstants.ROBOT_COUNT_OFFSET + i);
        }

        return robotCount;
    }

    public static RobotType getRobotTypeToSpawn(RobotController rc) throws GameActionException{
        RobotType[] types = RobotType.values();
        return types[rc.readBroadcast(MyConstants.SPAWN_TYPE_OFFSET)];
    }

    // This method will attempt to spawn in the given direction (or as close to it as possible)
    public static void trySpawn(RobotController rc, Direction d, RobotType type) throws GameActionException {
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


    public static boolean attack(RobotController rc, RobotInfo[] enemyRobots) throws GameActionException{
        if (rc.isWeaponReady()) {
            MapLocation myLocation = rc.getLocation();
            RobotInfo toAttack = enemyRobots[0];
            int closest = Integer.MAX_VALUE;

            for (RobotInfo enemy : enemyRobots) {
                int distanceToEnemy = myLocation.distanceSquaredTo(enemy.location);
                if (distanceToEnemy < closest) {
                    closest = distanceToEnemy;
                    toAttack = enemy;
                } else if (distanceToEnemy == closest) {
                    if (enemy.health < toAttack.health) {
                        toAttack = enemy;
                    }
                }
            }

            if (rc.canAttackLocation(toAttack.location)) {
                rc.attackLocation(toAttack.location);
                return true;
            }
        }
        return false;
    }
    public static ArrayList<MapLocation> calcMLine(RobotController rc, MapLocation goal) {
        Direction dirToGoal;
        ArrayList<MapLocation> mLine = new ArrayList<MapLocation>();
        MapLocation previousLocation = rc.getLocation();

        while (!previousLocation.equals(goal)) {
            mLine.add(previousLocation);
            dirToGoal = previousLocation.directionTo(goal);
            previousLocation = previousLocation.add(dirToGoal);
        }

        return mLine;
    }
    
    public static boolean harass(RobotController rc, RobotType[] targets) throws GameActionException {
		MapLocation myLocation = rc.getLocation();
		int sensorRange = rc.getType().sensorRadiusSquared;
		int attackRange = rc.getType().attackRadiusSquared;
		RobotInfo[] enemiesInSight = rc.senseNearbyRobots(sensorRange, rc.getTeam().opponent());
		RobotInfo[] enemiesInRange = rc.senseNearbyRobots(attackRange, rc.getTeam().opponent());

		Direction nextMove = null;

		// enemies in range
		if (enemiesInRange.length > 0){
			// shoot targets
			attackByType(rc, enemiesInRange, targets);
            return true;
		}

		// enemies in sight
		if (enemiesInSight.length > 0){
			Boolean safe = true;

			// can any of these guys hit me?
			for (RobotInfo enemy : enemiesInSight){
				int enemyRange = enemy.type.attackRadiusSquared;
				int distanceFromEnemy = myLocation.distanceSquaredTo(enemy.location);

				if (enemyRange >= distanceFromEnemy){
					safe = false;
				}
			}
			// kite
			nextMove = kiteDirection(rc, myLocation, enemiesInSight);
			if (nextMove != null){
				rc.move(nextMove);
                return true;
			}
		}

		return false;
	}


	private static Direction moveToLocation(RobotController rc, MapLocation loc) {
		// TODO Auto-generated method stub
		return null;
	}

	// Assign a value to all surrounding squares. The square with the lowest value is returned, directions towards
	// enemy HQ are given preference. Square value = (2 * damage going to take next turn in that square) - (damage 
	// robot can inflict). 
	private static Direction kiteDirection(RobotController rc, MapLocation baseSquare, RobotInfo[] enemiesInSight) {
		Direction dirToMove = null;
		int myRange = rc.getType().attackRadiusSquared;
		double[] squareValues = new double[8];
		double lowestVal = 0.0;

		// base case
		for (RobotInfo enemy : enemiesInSight){
			int enemyRange = enemy.type.attackRadiusSquared;
			int distanceFromEnemy = baseSquare.distanceSquaredTo(enemy.location);

			// if an enemy can hit me, add 2 * its damage to this square
			if (enemyRange >= distanceFromEnemy){
				lowestVal += (2 * enemy.type.attackPower);
			}
			// if I can hit an enemy, add 1 * my damage to this square
			if (distanceFromEnemy <= myRange){
				lowestVal -= (rc.getType().attackPower);
			}
		}

		// for each surrounding square
		for (Direction dir : Direction.values()){
			int index = directionToInt(dir);
			// determine value of each square
			for (RobotInfo enemy : enemiesInSight){
				int enemyRange = enemy.type.attackRadiusSquared;
				int distanceFromEnemy = baseSquare.add(dir).distanceSquaredTo(enemy.location);
				// if an enemy can hit me, add 2 * its damage to this square
				if (enemyRange >= distanceFromEnemy){
					squareValues[index] += (2 * enemy.type.attackPower);
				}
				// if I can hit an enemy, add 1 * my damage to this square
				if (distanceFromEnemy <= myRange){
					squareValues[index] -= (rc.getType().attackPower);
				}
			}
			// if this direction is safer than last one, set to move there
			if (squareValues[index] <= lowestVal && rc.canMove(dir)){
				lowestVal = squareValues[index];
				dirToMove = intToDirection(index);
			}	
		}
		return dirToMove;
	}
	private static void attackByType(RobotController rc, RobotInfo[] enemies, RobotType[] targetTypes) throws GameActionException {
		if (!rc.isWeaponReady()){
			return;
		}
		RobotInfo target = enemies[0];
		double lowestHealth = enemies[0].health;

		for (RobotInfo enemy : enemies){
			for (RobotType tType : targetTypes){
				if (enemy.type == tType && enemy.health < lowestHealth){
					target = enemy;
					lowestHealth = target.health;	
				}
			}
		}
		if (rc.canAttackLocation(target.location)){
			rc.attackLocation(target.location);
		}
	}
}