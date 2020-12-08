package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import simulation.Environment;

public class VariablesPanel extends OverlayPanel {
    public ArrayList<SliderComponent> sliders;
    private static final Color OUTLINE = Color.black;
    
    public VariablesPanel(Panel p, Dimension d) {
        super(p, d);
        this.setTitle("Variables");
        this.sliders = new ArrayList<SliderComponent>();
        this.createSlider("Ticks Between Food Spawn", 0, 200, Environment.ticksBetweenFoodSpawn);
        this.createSlider("Mutation Rate", 0, 2, Environment.mutationRate);
    }

    @Override
    public void clicked(int x, int y) {
        for(SliderComponent slider : sliders) {
            if(slider.isClicked(x, y)) {
                slider.clicked(x, y);
            }
        }
        updateValues();
        
    }
    
    @Override
    public void render(Graphics g) {
        drawOutline(g, OUTLINE);
        arrangeSliders();
        for(SliderComponent slider : sliders) {
            slider.drawSlider(g, x, y);
        }
    }
    
    public void arrangeSliders() {
        int partition = this.partition(this.dimension.height - (2 * borderBuffer), sliders.size()) + 1;
        int currPart = partition/2;
        for(SliderComponent slider : sliders) {
            slider.setX(40);
            slider.setY(currPart);
            currPart += partition;
        }
    }
    
    public void createSlider(String title, int minValue, int maxValue, float initialVal) {
        SliderComponent s = new SliderComponent(minValue, maxValue, initialVal, this.dimension.width - 40 - borderBuffer * 2, 0, 0);
        s.setTitle(title);
        sliders.add(s);
        arrangeSliders();
        s.calcCurrPosition();
    }
    
    public SliderComponent getSlider(String title) {
        for(SliderComponent slider : sliders) {
            if(title.equals(slider.getTitle())) {
                return slider;
            }
        }
        return null;
    }
    
    public void updateValues() {
        Environment.mutationRate = getSlider("Mutation Rate").getCurrValue();
        Environment.ticksBetweenFoodSpawn = (int) getSlider("Ticks Between Food Spawn").getCurrValue();
    }
    

}
