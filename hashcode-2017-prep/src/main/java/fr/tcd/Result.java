package fr.tcd;

public class Result {
	private int[][] drones;

	public Result(int nbDrones, int nbTours) {
		drones = new int[nbDrones][nbTours];
		for (int i = 0; i < nbDrones; i++) {
			for (int j = 0; j < nbTours; j++) {
				
				drones[i][j] = 0;
			}
		}
	}

	public void addDroneTour(int droneNum, int tourNum, int deplacement) {
		drones[droneNum][tourNum] = deplacement;
	}

	public int[][] getDrones() {
		return drones;
	}
}
