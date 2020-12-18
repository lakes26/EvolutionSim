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

        this.createSlider("Tick Rate", 1, 120, (float) Environment.tickRate);        
        this.createSlider("Food Per Second", 0, 200, (float) Environment.foodPerSecond);
        this.createSlider("Trait Mutation Rate", 0, 1, Environment.traitMutationRate);
        this.createSlider("Network Mutation Probability", 0, 1, Environment.networkMutationProbability);
        this.createSlider("Network Perturbation Probability", 0, 1, Environment.networkPerturbationProbability);
        this.createSlider("Network Perturbation Amount", 0, 3, Environment.networkPerturbationAmount);     
        this.createSlider("Network New Value Probability", 0, 1, Environment.networkNewValueProbability);
        this.createSlider("Network Value Random Range", 0, 10, Environment.networkValueRandRange);
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
        this.setTitle("Variables");
        this.fillBackgroud(g, new Color(245,245,245));
        super.render(g);
        
        arrangeSliders();
        for(SliderComponent slider : sliders) {
            slider.drawSlider(g, x, y);
        }
    }
    
    public void arrangeSliders() {
        int partition = this.partition(this.dimension.height - (2 * borderBuffer) - 30, 
        							   sliders.size()) + 1;
        int currPart = partition / 2;
        for(SliderComponent slider : sliders) {
            slider.setX(40);
            slider.setY(30 + currPart);
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
        Environment.tickRate = getSlider("Tick Rate").getCurrValue();
        Environment.foodPerSecond = getSlider("Food Per Second").getCurrValue();
    	Environment.traitMutationRate = getSlider("Trait Mutation Rate").getCurrValue();
    	Environment.networkMutationProbability = getSlider("Network Mutation Probability").getCurrValue();
    	Environment.networkPerturbationProbability = getSlider("Network Perturbation Probability").getCurrValue();
    	Environment.networkPerturbationAmount = getSlider("Network Perturbation Amount").getCurrValue();
    	Environment.networkNewValueProbability = getSlider("Network New Value Probability").getCurrValue();
    	Environment.networkValueRandRange = getSlider("Network Value Random Range").getCurrValue();
    }
}
