package simulation;

import java.util.ArrayList;
import java.util.HashMap;

public class VisibilityBox {
    public float boxSize = Agent.agentVision;
    private float height = Environment.height;
    private float width = Environment.width;

    private int xBoxes = (int) Math.floor(width/boxSize) + 1;
    private int yBoxes = (int) Math.floor(height/boxSize) + 1;
    private int nBoxes = xBoxes*yBoxes;

    private HashMap<Integer,ArrayList<CollidableObject>> boxes = new HashMap<>();

    public VisibilityBox(Environment env) {
        ArrayList<Agent> agents = env.getAgents();
        ArrayList<Food> food = env.getFood();

        for(int i = 0; i < nBoxes; i++) {
            boxes.put(i, new ArrayList<CollidableObject>());
        }

        for(Agent a : agents) {
            int box = boxNum(a);
            if (boxes.containsKey(box)) {
                ArrayList<CollidableObject> objects = boxes.get(box);
                objects.add(a);
            } else {
                ArrayList<CollidableObject> objects = new ArrayList<>();
                objects.add(a);
            }
        }

        for(Food f : food) {
            int box = boxNum(f);
            if (boxes.containsKey(box)) {
                ArrayList<CollidableObject> objects = boxes.get(box);
                objects.add(f);
            } else {
                ArrayList<CollidableObject> objects = new ArrayList<>();
                objects.add(f);
            }
        }
    }

    public ArrayList<CollidableObject> visibleCandidates(Agent a) {
        ArrayList<Integer> visBoxes = adjacentBoxes(a);
        ArrayList<CollidableObject> visibles = new ArrayList<>();

        for(Integer i : visBoxes) {
            ArrayList<CollidableObject> objects = boxes.get(i);
            if(objects.size()>0) {
                visibles.addAll(boxes.get(i));
            }
        }
        return visibles;
    }

    private ArrayList<Integer> adjacentBoxes(Agent a) {
        int boxNum = boxNum(a);

        int yBox = boxNum / xBoxes;
        int xBox = boxNum - yBox * xBoxes;
        ArrayList<Integer> allInts = new ArrayList<>();
        for (int j = 0; j < nBoxes; j++) {
            int y = j/xBoxes;
            int x = j - y*xBoxes;
            if (Math.max(Math.abs(yBox - y),Math.abs(xBox - x)) <= 1) {
                allInts.add(j);
            }
        }
        return allInts;
    }

    private int boxNum(int x, int y) {
        return y * xBoxes + x;
    }

    private int boxNum(CollidableObject o) {
        int xBox = (int) Math.floor(o.getX()/boxSize);
        int yBox = (int) Math.floor(o.getY()/boxSize);
        return boxNum(xBox, yBox);
    }

}
