import java.util.*;
import java.io.*;
import java.math.*;


class Player {

    public static void main(String args[]) {
        
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
	        
	public void Action(){
	    String command = "";
		
		//select best Factory to send troops from
		Factory temp = selectTEMP();
		
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
		
		//get two closest factories for use in cases
		Integer closest1 = temp.getClosestFactory();
		Integer closest2 = temp.getClosestFactory(closest1); //finds next closest factory
		
		
////////CASE 1: all bases except 2 are neutral (beginning), no Cyborgs in transit
			//send 3 cyborgs to the nearest two bases until it is under our control
			//send 1 cyborg all the other factories
		if(neutrals == factoryCount-2 && ourTroops==0){
			command += "MSG case 1, id="+temp.getID()+";";
			
			//send 3 to each
			command+= "MOVE " + temp.getID() + " " + closest1 + " " + "3"+";";
			command+= "MOVE " + temp.getID() + " " + closest2 + " " + "3"+";";
			
			//send 1 to rest
			for(Integer i : neutralIDs){
			    command+= "MOVE " + temp.getID() + " " + i + " " + "1"+";";
		    }
		}
		
			
////////CASE 2: Cyborgs we control are in transit, temp is 2 or less
		    //WAIT
		if(ourTroops>0 && temp.getCC()<=2){
		    command += "MSG case 2, id="+temp.getID()+";";
			command+= "WAIT;";
		}
		
//////////CASE 3: temp is 3 or more
           
           
          
		if(temp.getCC()>=2){
		    temp = selectTEMP();
		    command += "MSG case 3, id="+temp.getID()+";";
			if(temp.getCC()<=3){
			    command+= "MOVE " + temp.getID() + " " + closest1 + " " + "2"+";";
			    command+= "MOVE " + temp.getID() + " " + closest2 + " " + "1"+";";
			}
			if(temp.getCC()==4){
			    command+= "MOVE " + temp.getID() + " " + closest1 + " " + "2"+";";
			    command+= "MOVE " + temp.getID() + " " + closest2 + " " + "2"+";";
			}
			if(temp.getCC()>4){
			    command+= "MOVE " + temp.getID() + " " + closest1 + " " + "2"+";";
			    command+= "MOVE " + temp.getID() + " " + closest2 + " " + "2"+";";
			    for(Integer i : enemyIDs){
			        command+= "MOVE " + temp.getID() + " " + i + " " + "2"+";";
		        }
		        for(Integer i : neutralIDs){
			        command+= "MOVE " + temp.getID() + " " + i + " " + "2"+";";
		        }
			}
			
			for(int i = 0; i<controlledIDs.size(); i++){
			    Factory c = getFactoryByID(controlledIDs.get(i));
			    Integer _closest1 = c.getClosestFactory();
	        	Integer _closest2 = c.getClosestFactory(_closest1); //finds next closest factory
			    
			    command+= "MOVE " + c.getID() + " " + _closest1 + " " + "3"+";";
			    command+= "MOVE " + c.getID() + " " + _closest2 + " " + "3"+";";
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