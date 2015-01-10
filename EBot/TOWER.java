package EBot;

import battlecode.common.*;

public class TOWER {
	
	
	static Team myTeam;
	static Team enemyTeam;
	static int myRange;
	
	
    public static void execute(RobotController rc) throws GameActionException {
    	if(rc.isWeaponReady())
    		attackSomething(rc);
    		
    }
    
    
    
    
    
 // This method will attack an enemy in sight, if there is one
 	static void attackSomething(RobotController rc) throws GameActionException {
 		myRange = rc.getType().attackRadiusSquared;
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
 		
 		RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
 		if (enemies.length > 0) {
 			rc.attackLocation(enemies[0].location);
 		}
 	}
}