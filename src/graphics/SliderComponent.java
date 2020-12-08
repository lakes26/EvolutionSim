package graphics;

import java.awt.Font;
import java.awt.Graphics;

public class SliderComponent {
    private float minValue, maxValue, range, currPosition, currValue, length, x, y;
    private String title;
    private static int selectorSize = 10;
    private static int lineHitboxHeight = 6;

    public SliderComponent(float minValue, float maxValue, float initialVal, float length, float x, float y) {
        this.range = maxValue - minValue;
        this.currValue = initialVal;
        this.length = length;
        this.currPosition = 0;
        this.x = x;
        this.y = y;
    }
    
    public void drawSlider(Graphics g, float panelX, float panelY) {
        float realX = panelX + x;
        float realY = panelY + y;
        g.drawLine((int) realX, (int)realY, (int)(realX + length), (int)realY);
        g.fillOval((int)(realX + currPosition), (int) realY - (selectorSize / 2), selectorSize, selectorSize);
        g.setFont(new Font("TimesNewRoman", Font.PLAIN, 12));
        if(this.title != null) g.drawString(this.title, (int)realX, (int)realY - 10);
        g.setFont(new Font("TimesNewRoman", Font.PLAIN, 10));
        g.drawString(String.format("%.2f", currValue), (int)realX - 30, (int) realY + 5);
    }
    
    public boolean isClicked(int clickX, int clickY) {
        if(clickX > x && clickX < x + length && clickY < y + lineHitboxHeight && clickY > y - lineHitboxHeight) {
            return true;
        }
        return false;
    }
    
    public void clicked(int clickX, int clickY) {
        this.currPosition = (float) clickX - x;
        this.calcCurrValue();
    }
    
    public void calcCurrValue() {
        this.currValue = ((currPosition / length) * range) + minValue;
    }
    
    public void calcCurrPosition() {
        this.currPosition = ((currValue - minValue) / range) * length;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getCurrValue() {
        return currValue;
    }
    
    public String getTitle() {
        return title;
    }
    
    
    
    
    
}
