import java.util.*;
import java.io.*;
import java.math.*;
import java.lang.Math;


class Player {

    public static void main(String args[]) {
        
        //String command = "";
        Scanner in = new Scanner(System.in);
        Integer factoryCount = in.nextInt(); // the number of factories
        FactoryManager manager = new FactoryManager(factoryCount); // main manager
        Integer count = 0;
        Integer linkCount = in.nextInt(); // the number of links between factories
        
        for (Integer i = 0; i < linkCount; i++) {
            count++;
            Integer factory1 = in.nextInt();
            Integer factory2 = in.nextInt();
            Integer distance = in.nextInt();
            
            
            if(manager.getFactories().size() == 0){
				//create first 2 Factories and add the entries
				Factory newFactory = new Factory(factory1);
				newFactory.addMapEntry(factory2, new Info(distance, 0));
				
				Factory newFactory2 = new Factory(factory2);
				newFactory2.addMapEntry(factory1, new Info(distance, 0));
				
                manager.addFactory(newFactory);   
                manager.addFactory(newFactory2);
            }
            
            if(manager.getFactoryByID(factory1) == null){
                Factory newFactory3 = new Factory(factory1);
				newFactory3.addMapEntry(factory2, new Info(distance, 0));
			    manager.addFactory(newFactory3);
            }
            else if(manager.getFactoryByID(factory1) != null && manager.getFactoryByID(factory1).getDistances().get(factory2) == null){
                manager.getFactoryByID(factory1).addMapEntry(factory2, new Info(distance, 0));
            }
				
			if(manager.getFactoryByID(factory2) == null){
				Factory newFactory4 = new Factory(factory2);
				newFactory4.addMapEntry(factory1, new Info(distance, 0));
				manager.addFactory(newFactory4);
			}
			else if(manager.getFactoryByID(factory2) != null && manager.getFactoryByID(factory2).getDistances().get(factory1) == null){
			    manager.getFactoryByID(factory2).addMapEntry(factory1, new Info(distance, 0));
			}
        }
        
        //fill in entries
        for(Factory f: manager.getFactories()){
            for (Map.Entry<Integer, Info> entry : f.getDistances().entrySet()){
			    for(Factory fp: manager.getFactories()){
			        if(fp.getID() == entry.getKey()){
			            fp.addMapEntry(f.getID(), new Info(entry.getValue().getDistance(), 0));
			        }
			    }
            }
		}
		
		String kk = "";
		for(Factory f : manager.getFactories()){
		    kk += f.getID()+" ";
		}
		//System.out.println(kk);
        

        // game loop
        while (true) {
            
            manager.clearTroops();
            
            Integer entityCount = in.nextInt(); // the number of entities
            for (Integer i = 0; i < entityCount; i++) {
                Integer entityId = in.nextInt();
                String entityType = in.next();
                Integer arg1 = in.nextInt();
                Integer arg2 = in.nextInt();
                Integer arg3 = in.nextInt();
                Integer arg4 = in.nextInt();
                Integer arg5 = in.nextInt();
                
                if(entityType.equals("FACTORY")){
                    for(Factory f : manager.getFactories()){
						if(f.getID() == entityId){
							f.setC(arg1);
							f.setCC(arg2);
							f.setProduction(arg3);
						}
					}
				}
				
				if(entityType.equals("TROOP")){
				    Troop t = new Troop(arg1, arg2, arg3, arg4, arg5);
				    manager.addTroop(t);
				}
				    
                //fill in production values for each individual Factory object's Map
                for(Factory f : manager.getFactories()){
                     for (Map.Entry<Integer, Info> entry : f.getDistances().entrySet()){
                         Factory currentMapFactory = manager.getFactoryByID(entry.getKey());
                         f.getDistances().put(entry.getKey(), new Info(entry.getValue().getDistance(), currentMapFactory.getProduction()));
                     }
                }
                
            }
            String test = "";
            for(Factory f : manager.getFactories()){
                test += f.getID() + " "+f.getDistances()+"     ";
            }
            manager.Action();
        }
    }
}


class FactoryManager{
	private ArrayList<Factory> factories;
	private ArrayList<Troop> troops;
	private Integer factoryCount;
	Integer turnCount = 0;
	
	public FactoryManager(){
		factories = new ArrayList<Factory>();
		troops = new ArrayList<Troop>();
	}
	
	public FactoryManager(Integer c){
		factories = new ArrayList<Factory>();
		troops = new ArrayList<Troop>();
		factoryCount = c;
	}
	
	
	public void addFactory(Factory f){
		factories.add(f);
	}
	
	public ArrayList<Factory> getFactories(){
		return factories;
	}
	
	public void addTroop(Troop t){
	    troops.add(t);
	}
	
	public ArrayList<Troop> getTroops(){
	    return troops;
	}
	
	public void clearTroops(){
	    troops.clear();
	}
	
	public Factory selectTEMP(){
	    Factory t = new Factory(0, 0, 0, 0, 0);
		for(Factory f : factories){
			//gives priority to the controlled Factory with the most cyborgs
			if(f.getC() == 1 && f.getCC() > t.getCC()){
				t = f;
			}
		}
		return t;
	}
	
	//returns null if the factory does not exist
	public Factory getFactoryByID(Integer i){
	    for(Factory f : factories){
			if(f.getID() == i){
				return f;
			}
	    }
	    return null;
	}
	
	//returns the closest enemy factory (ID) from the given Factory
	public Integer getClosestEnemyFactory(Factory c){
	    Integer dist = -1;
	    Integer closest = -1;
	    for (Map.Entry<Integer, Info> entry : c.getDistances().entrySet()){
	        Factory f = getFactoryByID(entry.getKey());
	        if(dist == -1 && f.getC() == -1){
	            dist = c.distanceTo(f);
	            closest = f.getID();
	        }
	        else if(f.getC() == -1 && c.distanceTo(f)<dist){
	            dist = c.distanceTo(f);
	            closest = f.getID();
	        }
	    }
	    return closest;
	}
	
	//returns the closest ally factory (ID) from the given Factory
	public Integer getClosestAllyFactory(Factory c){
	    Integer dist = -1;
	    Integer closest = -1;
	    for (Map.Entry<Integer, Info> entry : c.getDistances().entrySet()){
	        Factory f = getFactoryByID(entry.getKey());
	        if(dist == -1 && f.getC() == 1){
	            dist = c.distanceTo(f);
	            closest = f.getID();
	        }
	        else if(f.getC() == 1 && c.distanceTo(f)<dist){
	            dist = c.distanceTo(f);
	            closest = f.getID();
	        }
	    }
	    return closest;
	}
	
	//returns the closest neutral factory (ID) from the given Factory
	public Integer getClosestNeutralFactory(Factory c){
	    Integer dist = -1;
	    Integer closest = -1;
	    for (Map.Entry<Integer, Info> entry : c.getDistances().entrySet()){
	        Factory f = getFactoryByID(entry.getKey());
	        if(dist == -1 && f.getC() == 0){
	            dist = c.distanceTo(f);
	            closest = f.getID();
	        }
	        else if(f.getC() == 0 && c.distanceTo(f)<dist){
	            dist = c.distanceTo(f);
	            closest = f.getID();
	        }
	    }
	    return closest;
	}
	
	public String CASE1(Factory f,ArrayList<Integer> enemyIDs_, Integer best1_, Integer best2_){
	    String AddedCommand = "";
	    AddedCommand += "MSG case 1, id="+f.getID()+";";
			
		if(f.getCC() > 16){
		    AddedCommand += "INC "+f.getID()+";";
		}
		//send bomb to enemy's intial base
		AddedCommand += "BOMB " + f.getID()+ " "+ enemyIDs_.get(0) + ";";

		//send 3 to each
		AddedCommand+= "MOVE " + f.getID() + " " + best1_ + " " + "5"+";";
		
		return AddedCommand;
		
	}
	
	public String CASE2(Factory f){
	    String AddedCommand = "";
		AddedCommand+= "WAIT;";
		return AddedCommand;
	}
	
	public String CASE3(Factory f, Integer best1_, Integer best2_){
	    String AddedCommand = "";          
	    AddedCommand += "MSG case 3, id="+f.getID()+";";
	    AddedCommand+= "MOVE " + f.getID() + " " + best1_ + " " + "2"+";";
	    AddedCommand+= "MOVE " + f.getID() + " " + best2_ + " " + "1"+";";
	    return AddedCommand;
	}
	
	public String CASE4(Factory f, Integer best1_, Integer best2_){
	    String AddedCommand = "";
	    AddedCommand += "MSG case 4, id="+f.getID()+";";
	    AddedCommand+= "MOVE " + f.getID() + " " + best1_ + " " + "2"+";";
	    AddedCommand+= "MOVE " + f.getID() + " " + best2_ + " " + "2"+";";
	    return AddedCommand;
	}      

    public String CASE5(Factory f, Integer best1_, Integer best2_, ArrayList<Integer> enemyIDs_, ArrayList<Integer> neutralIDs_){
        String AddedCommand = "";
        //AddedCommand += "MSG case 5, id="+f.getID()+";";
        Integer value = f.getCC();
        double val = (double) value;
        
        double sTB1 = val * 0.3;
        double sTB2 = val * 0.25;
        
        int sendToBest1 = (int) sTB1;
        int sendToBest2 = (int) sTB2;
        
        AddedCommand+= "MOVE " + f.getID() + " " + best1_ + " " + sendToBest1 +";";
	    AddedCommand+= "MOVE " + f.getID() + " " + best2_ + " " + sendToBest2 +";";
	    
	    double sTN = val - (2*sendToBest1);
	    int sendToNeutrals = (int) sTN;
	    
	    
	    
	    return AddedCommand;
    }
	
	public void Action(){
	    String command = "";
	    turnCount+= 2;
        Integer Csent = 0;
		
		//count how many neutral bases and store their ID
		Integer neutrals = 0;
		ArrayList<Integer> neutralIDs = new ArrayList<Integer>();
		for(Factory f : factories){
			if(f.getC() == 0){
				neutralIDs.add(f.getID());
				neutrals++;
			}
		}
        
        //count how many enemy bases and store their ID
        Integer enemies = 0;
		ArrayList<Integer> enemyIDs = new ArrayList<Integer>();
		for(Factory f : factories){
			if(f.getC() == -1){
				enemyIDs.add(f.getID());
				enemies++;
			}
		}
		
		//count how many controlled bases and store their ID
		Integer controlled = 0;
		ArrayList<Integer> controlledIDs = new ArrayList<Integer>();
		for(Factory f : factories){
			if(f.getC() == 1){
				controlledIDs.add(f.getID());
				controlled++;
			}
		}
		
		//count how many troops are in transit
        Integer ourTroops = 0;
		for(Troop t : troops){
		    if(t.getC() == 1){
		        ourTroops++;
		    }
		}
		
        //for each controlled factory, select the right case
	    for(int i = 0; i<controlledIDs.size(); i++){
	        
	        Factory c = getFactoryByID(controlledIDs.get(i));
	        
	        //get two closest factories for use in cases
    		Integer closest1 = c.getClosestFactory();
    		Integer closest2 = c.getClosestFactory(closest1); //finds next closest factory
    		
    		//get two best factories for use in cases
    		Integer best1 = c.getBestFactory();
    		Integer best2 = c.getBestFactory(best1); //finds second best factory
    		
    		
    		//inc if possible
    		if(getFactoryByID(i).getProduction() < 3 && getFactoryByID(i).getCC() >= 11 && getFactoryByID(i).getC() == 1){
		        if(command.indexOf("INC "+i+";") == -1){
		            command += "INC "+i+";";
		        }
		    }
	        command+="MSG " + turnCount + ";";
	        if (turnCount < 20) {
	            if(neutrals == factoryCount-2 && ourTroops==0){    //beginning of game
    	            command += CASE1(c, enemyIDs, best1, best2);
    	        }
                else if(ourTroops>0 && c.getCC()<=2){           //c <= 2
    	            command += CASE2(c);
                }
    		    else if(c.getCC()==3){                             //c == 3
    		        command += CASE3(c, best1, best2);
    		    }
    		    else if(c.getCC()==4){
    		        command += CASE4(c, best1, best2);             //c == 4 
    		    }
    		    else if(c.getCC()>=5){                             //c <= 5
    		        command += CASE5(c, best1, best2, enemyIDs, neutralIDs);
    		    }
	        }
	       else {
	           boolean noGoodN = true;
	            //check to see if there are any neutral factories 
	            //with 1 production or more that we missed
	           for (Integer n : neutralIDs) {
	               if (getFactoryByID(n).getProduction() != 0) {
	                       noGoodN = false;
	                   }
	           }
	           //if there are, find the closest ally factory and have it send exactly the right amount to it
	           if (!noGoodN){
	               for (Integer b : neutralIDs) {
	                   if (getFactoryByID(b).getProduction() != 0) {
	                       Integer s = getFactoryByID(b).getCC();
	                       s += 1;
	                        command+="MOVE " + getClosestAllyFactory(getFactoryByID(b)) + " " + b + " " + s + ";";
	                   }
	               }
	           }
	           //begin stage 2 cases
	       }
	   }
		
		//END OF GAME CASE DON'T CHANGE
		if(command.length() == 0){
		    command += "WAIT;";
		}
		
		System.out.println(command.substring(0, command.length()-1));	
	}
}


class Factory{
	private Integer closest_factory_distance;
	private Integer controlled;
	private Integer cyborg_count;
	private Integer production;
	
	private Map<Integer, Info> distances;
	
	private Integer ID;
	
	public Factory(Integer cfd, Integer c, Integer cc, Integer p, Integer i){
		closest_factory_distance = cfd;
		controlled = c;
		cyborg_count = cc;
		production = p;
		ID = i;
		distances = new HashMap<Integer, Info>();
	}
	
	public Factory(Integer i){
	    closest_factory_distance = null;
		controlled = null;
		cyborg_count = null;
		production = null;
		ID = i;
		distances = new HashMap<Integer, Info>();
	}
	
	public Factory(){
	    closest_factory_distance = null;
		controlled = null;
		cyborg_count = null;
		production = null;
		ID = null;
		distances = new HashMap<Integer, Info>();
	}
	
	public void setCFD(Integer d){
		closest_factory_distance = d;
	}
	
	public void setC(Integer c){
		controlled = c;
	}
	
	public void setCC(Integer cc){
		cyborg_count = cc;
	}
	
	public void setProduction(Integer p){
		production = p;
	}
	
	public Integer getCFD(){
		return closest_factory_distance;
	}
	
	public Integer getC(){
		return controlled;
	}
	
	public Integer getCC(){
		return cyborg_count;
	}
	
	public Integer getProduction(){
		return production;
	}
	
	public Integer getID(){
		return ID;
	}
	
	public Map<Integer, Info> getDistances(){
		return distances;
	}
	
	public void addMapEntry(Integer i, Info d){
		distances.put(i, d);
	}
	
	public Integer getClosestFactory(){
	    Integer dist = -1;
	    Integer closest = -1;
	    for (Map.Entry<Integer, Info> entry : distances.entrySet()){
	        if(dist == -1){
			    dist = entry.getValue().getDistance();
			    closest = entry.getKey();
	        }
	        else if(entry.getValue().getDistance() < dist){
	            dist = entry.getValue().getDistance();
	            closest = entry.getKey();
	        }
		}
		return closest;
	}
	
	//exclude a specific factory from the possible factories
	public Integer getClosestFactory(Integer i){
	    Integer dist = -1;
	    Integer closest = -1;
	    for (Map.Entry<Integer, Info> entry : distances.entrySet()){
	        if(dist == -1 && entry.getKey()!= i){
			    dist = entry.getValue().getDistance();
			    closest = entry.getKey();
	        }
	        else if(entry.getValue().getDistance() < dist && entry.getKey()!= i){
	            dist = entry.getValue().getDistance();
	            closest = entry.getKey();
	        }
		}
		return closest;
	}	
	
	//find best factory in terms of distance and production
	public Integer getBestFactory(){
	    double score = 0;
	    Integer best = -1;
	    for (Map.Entry<Integer, Info> entry : distances.entrySet()){
	        if(best== -1){
	            best = entry.getKey();
	            //weighing formula is here
	            score = 1.0 * Math.pow(entry.getValue().getProduction(), 1.5) / entry.getValue().getDistance();
	        }
	        else if((1.0 * Math.pow(entry.getValue().getProduction(), 1.5) / entry.getValue().getDistance()) > score){
	            best = entry.getKey();
	            score = 1.0 * Math.pow(entry.getValue().getProduction(), 1.5) / entry.getValue().getDistance();
	        }
	    }
	    return best;
	}
	
	//find best factory in terms of distance and production
	public Integer getBestFactory(Integer i){
	    double score = 0;
	    Integer best = -1;
	    for (Map.Entry<Integer, Info> entry : distances.entrySet()){
	        if(best== -1 && entry.getKey()!= i){
	            best = entry.getKey();
	            //weighing formula is here
	            score = 1.0 * Math.pow(entry.getValue().getProduction(), 2) / entry.getValue().getDistance();
	        }
	        else if((1.0 * Math.pow(entry.getValue().getProduction(), 2) / entry.getValue().getDistance()) > score && entry.getKey()!= i){
	            best = entry.getKey();
	            score = 1.0 * Math.pow(entry.getValue().getProduction(), 2) / entry.getValue().getDistance();
	        }
	    }
	    return best;
	}
	        
	
	public Integer distanceTo(Factory f){
		for (Map.Entry<Integer, Info> entry : distances.entrySet()){
			if(entry.getKey() == f.getID()){
				return entry.getValue().getDistance();
			}
		}
	    return null;

	}
}

class Troop{
    
    private int controlled;
    private int factorySource;
    private int factoryDest;
    private int cyborgCount;
    private int arrivalTime;
    
    public Troop(int c, int f, int f2, int c2, int a){
        controlled = c;
        factorySource = f;
        factoryDest = f2;
        cyborgCount = c2;
        arrivalTime = a;
    }
    
    public Integer getC(){
        return controlled;
    }
    
    public Integer getSource(){
        return factorySource;
    }
    
    public Integer getDest(){
        return factoryDest;
    }
    
    public Integer getCC(){
        return cyborgCount;
    }
    
    public Integer getA(){
        return arrivalTime;
    }
}

class Info{
    private Integer distance;
    private Integer production;
    
    public Info(Integer d, Integer p){
        distance = d;
        production = p;
    }
    
    public Integer getDistance(){
        return distance;
    }
    
    public Integer getProduction(){
        return production;
    }
    
}  