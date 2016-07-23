package dom.company.thesis.model;

public class Numbering {
	
	private int[] numPattern;
	private int[] assignmentPattern;
	
	//Counters
	private int lastNr;
	private int maxNr;
	private int total;
	private int consecutive;
	private int[] pert;
	
	//constraints
	private int maxTotal;
	private int minTotal;
	private int maxPert;
	private int minPert;
	private int maxBetween;
	private int minBetween;
	private int maxConsecutive;
	private int minConsecutive;
	
	//penalties
	private int penaltyMaxTotal;
	private int penaltyMinTotal;
	private int penaltyMaxPert;
	private int penaltyMinPert;
	private int penaltyMaxBetween;
	private int penaltyMinBetween;
	private int penaltyMaxConsecutive;
	private int penaltyMinConsecutive;
	
	//costs;
	private int costMaxTotal;
	private int costMinTotal;
	private int costMaxPert;
	private int costMinPert;
	private int costMaxBetween;
	private int costMinBetween;
	private int costMaxConsecutive;
	private int costMinConsecutive;
	
	public Numbering(int[] numPattern) {
		
		//Initialize patterns
		this.numPattern = numPattern;
		this.assignmentPattern = new int[numPattern.length];
		
		//Initialize counters
		this.lastNr = -1;
		this.maxNr=this.getMaxNr();
		this.total = 0;
		this.consecutive = 0;
		this.pert = new int[Math.max(maxNr+1,0)];
		
		//Initialize constraint values
		this.maxTotal = numPattern.length;
		this.minTotal = 0;
		this.maxPert = numPattern.length;
		this.minPert = 0;
		this.maxPert = numPattern.length;
		this.minPert = 0;
		this.maxBetween = numPattern.length;
		this.minBetween = 0;
		this.maxConsecutive = numPattern.length;
		this.minConsecutive = 0;
		
		//Initialize penalties
		this.penaltyMaxTotal = 0;
		this.penaltyMinTotal = 0;
		this.penaltyMaxPert = 0;
		this.penaltyMinPert = 0;
		this.penaltyMaxBetween = 0;
		this.penaltyMinBetween = 0;
		this.penaltyMaxConsecutive = 0;
		this.penaltyMinConsecutive = 0;
		
		//Initialize weight factors
		this.costMaxTotal = 1;
		this.costMinTotal = 1;
		this.costMaxPert = 1;
		this.costMinPert = 1;
		this.costMaxBetween = 1;
		this.costMinBetween = 1;
		this.costMaxConsecutive = 1;
		this.costMinConsecutive = 1;
	}
	
	private void intermediateEval() {
		
		for (int i = 0; i < numPattern.length; i++) {
			
			if (assignmentPattern[i] != 0 && numPattern[i] != Integer.MIN_VALUE) {
				
				int nr = numPattern[i];
				total++;
				
				if (nr == lastNr + 1) {
					consecutive++;
				}
				else if (nr > lastNr + 1) {
					
					if (consecutive < minConsecutive && consecutive != 0) {
						penaltyMinConsecutive += costMinConsecutive * (minConsecutive - consecutive);
					}
					if (consecutive > maxConsecutive && consecutive != 0) {
						penaltyMaxConsecutive += costMaxConsecutive * (maxConsecutive - consecutive);
					}
					if (nr - lastNr - 1 < minBetween) {
						penaltyMinBetween += costMinBetween * (minBetween - (nr - lastNr - 1));
					}
					if (nr - lastNr - 1 > maxBetween) {
						penaltyMaxBetween += costMaxBetween * ((nr - lastNr - 1) - maxBetween);
					}
					consecutive = 1;
				}
				pert[nr]++;
				lastNr = nr;
			}
		}
	}
	
	private void finalEval() {
		if (total > maxTotal) {
			penaltyMaxTotal += costMaxTotal * (total - maxTotal);
		}
		if (total < minTotal) {
			penaltyMinTotal += costMinTotal * (minTotal - total);
		}
		if (consecutive > maxConsecutive && consecutive != 0) {
			penaltyMaxConsecutive += costMaxConsecutive * (consecutive - maxConsecutive);
		}
		if (consecutive < minConsecutive && consecutive != 0) {
			penaltyMinConsecutive += costMinConsecutive * (minConsecutive - consecutive);
		}
		
		for (int t = 0; t < maxNr; t++) {
			
			if (pert[t] > maxPert) {
				penaltyMaxPert += costMaxPert * (pert[t] - maxPert);
			}
			if (pert[t] < minPert) {
				penaltyMinPert += costMinPert * (minPert - pert[t]);
			}
		}
		
		//TO-DO: penaltise max_between
		
	}
	
	public void evaluate() {
		if (this.maxNr != Integer.MIN_VALUE)
		{
			intermediateEval();
			finalEval();
		}
	}
	
	private int getMaxNr() {
		int i = this.numPattern.length -1;
		
		while (numPattern[i] == Integer.MIN_VALUE && i != 0) {
			i--;
		}
		return numPattern[i];
	}

	public int[] getPattern() {
		return numPattern;
	}

	public void setPattern(int[] numPattern) {
		this.numPattern = numPattern;
	}

	public int getLastNr() {
		return lastNr;
	}

	public void setLastNr(int lastNr) {
		this.lastNr = lastNr;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getConsecutive() {
		return consecutive;
	}

	public void setConsecutive(int consecutive) {
		this.consecutive = consecutive;
	}

	public int[] getPert() {
		return pert;
	}

	public void setPert(int[] pert) {
		this.pert = pert;
	}

	public int getPenaltyMaxTotal() {
		return penaltyMaxTotal;
	}

	public void setPenaltyMaxTotal(int penaltyMaxTotal) {
		this.penaltyMaxTotal = penaltyMaxTotal;
	}

	public int getPenaltyMinTotal() {
		return penaltyMinTotal;
	}

	public void setPenaltyMinTotal(int penaltyMinTotal) {
		this.penaltyMinTotal = penaltyMinTotal;
	}

	public int getPenaltyMaxPert() {
		return penaltyMaxPert;
	}

	public void setPenaltyMaxPert(int penaltyMaxPert) {
		this.penaltyMaxPert = penaltyMaxPert;
	}

	public int getPenaltyMinPert() {
		return penaltyMinPert;
	}

	public void setPenaltyMinPert(int penaltyMinPert) {
		this.penaltyMinPert = penaltyMinPert;
	}

	public int getPenaltyMaxBetween() {
		return penaltyMaxBetween;
	}

	public void setPenaltyMaxBetween(int penaltyMaxBetween) {
		this.penaltyMaxBetween = penaltyMaxBetween;
	}

	public int getPenaltyMinBetween() {
		return penaltyMinBetween;
	}

	public void setPenaltyMinBetween(int penaltyMinBetween) {
		this.penaltyMinBetween = penaltyMinBetween;
	}

	public int getPenaltyMaxConsecutive() {
		return penaltyMaxConsecutive;
	}

	public void setPenaltyMaxConsecutive(int penaltyMaxConsecutive) {
		this.penaltyMaxConsecutive = penaltyMaxConsecutive;
	}

	public int getPenaltyMinConsecutive() {
		return penaltyMinConsecutive;
	}

	public void setPenaltyMinConsecutive(int penaltyMinConsecutive) {
		this.penaltyMinConsecutive = penaltyMinConsecutive;
	}

	public int getCostMaxTotal() {
		return costMaxTotal;
	}

	public void setCostMaxTotal(int costMaxTotal) {
		this.costMaxTotal = costMaxTotal;
	}

	public int getCostMinTotal() {
		return costMinTotal;
	}

	public void setCostMinTotal(int costMinTotal) {
		this.costMinTotal = costMinTotal;
	}

	public int getCostMaxPert() {
		return costMaxPert;
	}

	public void setCostMaxPert(int costMaxPert) {
		this.costMaxPert = costMaxPert;
	}

	public int getCostMinPert() {
		return costMinPert;
	}

	public void setCostMinPert(int costMinPert) {
		this.costMinPert = costMinPert;
	}

	public int getCostMaxBetween() {
		return costMaxBetween;
	}

	public void setCostMaxBetween(int costMaxBetween) {
		this.costMaxBetween = costMaxBetween;
	}

	public int getCostMinBetween() {
		return costMinBetween;
	}

	public void setCostMinBetween(int costMinBetween) {
		this.costMinBetween = costMinBetween;
	}

	public int getCostMaxConsecutive() {
		return costMaxConsecutive;
	}

	public void setCostMaxConsecutive(int costMaxConsecutive) {
		this.costMaxConsecutive = costMaxConsecutive;
	}

	public int getCostMinConsecutive() {
		return costMinConsecutive;
	}

	public void setCostMinConsecutive(int costMinConsecutive) {
		this.costMinConsecutive = costMinConsecutive;
	}

	public int[] getAssignmentPattern() {
		return assignmentPattern;
	}

	public void setAssignmentPattern(int[] assignmentPattern) {
		this.assignmentPattern = assignmentPattern;
	}

	public int[] getNumPattern() {
		return numPattern;
	}

	public void setNumPattern(int[] numPattern) {
		this.numPattern = numPattern;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMinTotal() {
		return minTotal;
	}

	public void setMinTotal(int minTotal) {
		this.minTotal = minTotal;
	}

	public int getMaxPert() {
		return maxPert;
	}

	public void setMaxPert(int maxPert) {
		this.maxPert = maxPert;
	}

	public int getMinPert() {
		return minPert;
	}

	public void setMinPert(int minPert) {
		this.minPert = minPert;
	}

	public int getMaxBetween() {
		return maxBetween;
	}

	public void setMaxBetween(int maxBetween) {
		this.maxBetween = maxBetween;
	}

	public int getMinBetween() {
		return minBetween;
	}

	public void setMinBetween(int minBetween) {
		this.minBetween = minBetween;
	}

	public int getMaxConsecutive() {
		return maxConsecutive;
	}

	public void setMaxConsecutive(int maxConsecutive) {
		this.maxConsecutive = maxConsecutive;
	}

	public int getMinConsecutive() {
		return minConsecutive;
	}

	public void setMinConsecutive(int minConsecutive) {
		this.minConsecutive = minConsecutive;
	}
	
	
	

}
