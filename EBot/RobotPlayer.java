package EBot;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {

	public static void run(RobotController rc) {

		while(true) {
			try {
				rc.setIndicatorString(0, "This is an indicator string.");
				rc.setIndicatorString(1, "I am a " + rc.getType());
			} catch (Exception e) {
				System.out.println("Unexpected exception");
				e.printStackTrace();
			}

			switch (rc.getType()) {
				case RobotPlayer.AEROSPACELAB:
					try {
						AEROSPACE.execute(rc);
					} catch (Exception e) {
						System.out.println("AEROSPACELAB Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.BARRACKS:
					try {
						BARRACKS.execute();
					} catch (Exception e) {
						System.out.println("BARRACKS Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.BASHER:
					try {
						BASHER.execute();
					} catch (Exception e) {
						System.out.println("BASHER Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.BEAVER:
					try {
						BEAVER.execute();
					} catch (Exception e) {
						System.out.println("BEAVER Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.COMMANDER:
					try {
						COMMANDER.execute();
					} catch (Exception e) {
						System.out.println("COMMANDER Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.COMPUTER:
					try {
						COMPUTER.execute();
					} catch (Exception e) {
						System.out.println("COMPUTER Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.DRONE:
					try {
						DRONE.execute();
					} catch (Exception e) {
						System.out.println("DRONE Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.HANDWASHSTATION:
					try {
						HANDWASHSTATION.execute();
					} catch (Exception e) {
						System.out.println("HANDWASHSTATION Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.HELIPAD:
					try {
						HELIPAD.execute();
					} catch (Exception e) {
						System.out.println("HELIPAD Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.HQ:
					try {
						HQ.execute();
					} catch (Exception e) {
						System.out.println("HQ Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.LAUNCHER:
					try {
						LAUNCHER.execute();
					} catch (Exception e) {
						System.out.println("LAUNCHER Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.MINER:
					try {
						MINER.execute();
					} catch (Exception e) {
						System.out.println("MINER Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.MINERFACTORY:
					try {
						MINERFACTORY.execute();
					} catch (Exception e) {
						System.out.println("MINERFACTORY Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.MISSILE:
					try {
						MISSILE.execute();
					} catch (Exception e) {
						System.out.println("MISSILE Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.SOLDIER:
					try {
						SOLDIER.execute();
					} catch (Exception e) {
						System.out.println("SOLDIER Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.SUPPLYDEPOT:
					try {
						SUPPLYDEPOT.execute();
					} catch (Exception e) {
						System.out.println("SUPPLYDEPOT Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.TANK:
					try {
						TANK.execute();
					} catch (Exception e) {
						System.out.println("TANK Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.TANKFACTORY:
					try {
						TANKFACTORY.execute();
					} catch (Exception e) {
						System.out.println("TANKFACTORY Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.TECHNOLOGYINSTITUTE:
					try {
						TECHNOLOGYINSTITUTE.execute();
					} catch (Exception e) {
						System.out.println("TECHNOLOGYINSTITUTE Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.TOWER:
					try {
						TOWER.execute();
					} catch (Exception e) {
						System.out.println("TOWER Exception");
						e.printStackTrace();
					}
					break;
				case RobotPlayer.TRAININGFIELD:
					try {
						TRAININGFIELD.execute();
					} catch (Exception e) {
						System.out.println("TRAININGFIELD Exception");
						e.printStackTrace();
					}
					break;
			}
			rc.yield();
		}
	}
}
