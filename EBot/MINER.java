package EBot;

import battlecode.common.*;

public class MINER {
	static Team myTeam;
	static Team enemyTeam;
	static int myRange;
	
	
    public static void execute(RobotController rc) throws GameActionException {
    	mineOrMove(rc);
    }
    
    
    
    static void attackSomething(RobotController rc) throws GameActionException {
 		RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
 		if (enemies.length > 0) {
 			rc.attackLocation(enemies[0].location);
 		}
 	}
    
    
    
    public static Direction[] getDirectionsToward(RobotController rc, MapLocation dest){
    	Direction toDest = rc.getLocation().directionTo(dest);
    	Direction[] dirs = {toDest, 
    						toDest.rotateLeft(), 
    						toDest.rotateRight(), 
    						toDest.rotateLeft().rotateLeft(), 
    						toDest.rotateRight().rotateRight()};
    	
    	return dirs;
    }
    
    public static Direction getMoveDir(RobotController rc, MapLocation dest){
    	Direction[] dirs = getDirectionsToward(rc, dest);
    	for (Direction d: dirs) {
    		if (rc.canMove(d)) {
    			return d;
    		}
    	}
    	return null;
    }
    
    private static void buildUnit(RobotController rc, RobotType type) throws GameActionException {
		if(rc.getTeamOre()>type.oreCost){
			Direction buildDir = getRandomDirection();
			if(rc.isCoreReady()&&rc.canBuild(buildDir, type)){
				rc.build(buildDir, type);
			}
		}
	}
    
private static Direction getRandomDirection() {
		double rand = Math.random() * 8;
    	rand = (int)rand;
		return Direction.values()[(int)rand];
	}
    
    
    
    public static void mineOrMove(RobotController rc) throws GameActionException{
    	
    	double rand = Math.random() * 2;
    	rand = (int)rand;
    	
    	if(rand == 0){
	    	if ((rc.senseOre(rc.getLocation())>10) && rc.isCoreReady() && rc.canMine()){
	    		rc.mine();
	    		mineOrMove(rc);
	    	}
	    	else
	    		toRally(rc);
    	}
    	else
    		toRally(rc);
    }
    
    
    
    public static void wander(RobotController rc) throws GameActionException{
    	move(rc, getRandomDirection());
    }
    
    
    public static void chargeHQ(RobotController rc) throws GameActionException{
    	
    	move(rc, getMoveDir(rc, rc.senseEnemyHQLocation()));
    }
    
    	
    public static void move(RobotController rc, Direction Dir) throws GameActionException{
    	if(rc.isCoreReady() && rc.canMove(Dir))
			rc.move(Dir);
    }
    
    public static void toRally(RobotController rc) throws GameActionException{
    	MapLocation hq = rc.senseHQLocation();
    	MapLocation ehq = rc.senseEnemyHQLocation();
    	int x = 0;
    	int y = 0;
    	if(getMoveDir(rc,ehq).equals(Direction.NORTH_EAST)){
    		x = hq.x + 12;
    		y = hq.y - 12;	
    	}
    	else if(getMoveDir(rc,ehq).equals(Direction.SOUTH_EAST)){
    		x = hq.x + 12;
    		y = hq.y + 12;	
    	}
    	else if(getMoveDir(rc,ehq).equals(Direction.NORTH_WEST)){
    		x = hq.x - 12;
    		y = hq.y - 12;	
    	}
    	else if(getMoveDir(rc,ehq).equals(Direction.SOUTH_WEST)){
    		x = hq.x - 12;
    		y = hq.y + 12;	
    	}
    	
    	MapLocation yolo = new MapLocation(x,y);
    	
    	move(rc, getMoveDir(rc, yolo));
    }
    
    
    
}



