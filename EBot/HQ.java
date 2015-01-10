package EBot;

import battlecode.common.*;

import java.util.Random;

public class HQ {
	static Team myTeam;
	static Team enemyTeam;
	static int myRange;

	public static void execute(RobotController rc) throws GameActionException {
		if(Clock.getRoundNum()<300)
			spawnUnit(rc, RobotType.BEAVER);
    }
	
	
	private static void spawnUnit(RobotController rc, RobotType type) throws GameActionException {
		Direction randomDir = getRandomDirection();
		if(rc.isCoreReady()&&rc.canSpawn(randomDir, type)){
			rc.spawn(randomDir, type);
		}
	}

	private static Direction getRandomDirection() {
		
		double rand = Math.random() * 8;
    	rand = (int)rand;
		return Direction.values()[(int)rand];
	}
	
	
	
}