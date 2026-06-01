package io.github.some_example_name;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.Vector;

public class InvenPlanet {
    float x;
    float y;
    int r;
    Vector2 position = new Vector2();
    Vector2 vel = new Vector2();
    Vector2 grav = new Vector2();

    public InvenPlanet(int x, int y, int r){
        this.x = x;
        this.y = y;
        this.r = r;
        position.set(x,y);
        vel.set(0,0);
        grav.set(0,-0.1f);
    }

    public void applyGrav(){ // collision issue is caused by gravity
        vel.add(grav);
    }

    public boolean checkLost(){
        if(position.y<-r){
            return true;
        }else{
            return false;
        }
    }

    public void updatePos(){
        position.add(vel);
        vel.scl(0.99f);
    }

    public void draw(ShapeRenderer sr){
        sr.circle(position.x,position.y,r);
    }

    public boolean checkColPlanet(InvenPlanet target){
        Vector2 targetPos;
        targetPos = target.position;
        float difference;
        difference = position.dst(targetPos);
        if(difference>r+target.r){
            return false;
        }else{
            return true;
        }
    }

    public void hitCirc(InvenPlanet target) {

        Vector2 delta = new Vector2(position).sub(target.position);
        float dist = delta.len();

        float overlap = (r + target.r) - dist;

        if (overlap > 0) {
            delta.nor();

            position.add(delta.cpy().scl(overlap*r/50));
            target.position.sub(delta.cpy().scl(overlap*r/50));


            float vector1 = vel.dot(delta);
            float vector2 = target.vel.dot(delta);

            if (vector1 > 0) vel.sub(delta.cpy().scl(vector1));
            if (vector2 < 0) target.vel.sub(delta.cpy().scl(vector2));

        }
    }

    public boolean checkColBox(Box box) {
        float closestX = Math.max(box.x, Math.min(position.x,box.x+box.w));
        float closestY = Math.max(box.y, Math.min(position.y,box.y+box.h));

        float dx = position.x-closestX;
        float dy = position.y-closestY;

        return (dx*dx+dy*dy) < (r*r);
    }

    public void hitBox(Box box) {
        float closestX = Math.max(box.x, Math.min(position.x, box.x + box.w));
        float closestY = Math.max(box.y, Math.min(position.y, box.y + box.h));

        float dx = position.x - closestX;
        float dy = position.y - closestY;

        float overlapX = r - Math.abs(dx);
        float overlapY = r - Math.abs(dy);

        if (overlapX < overlapY) {
            if (dx > 0) {
                position.x += overlapX;
            } else {
                position.x -= overlapX;
            }
            vel.x = 0;

        } else {
            if (dy > 0) {
                position.y += overlapY;
            } else {
                position.y -= overlapY;
            }
            vel.y = 0;
        }
    }

    public boolean overlap(float x, float y) {
        float dx = position.x - x;
        float dy = position.y - y;
        return (dx * dx + dy * dy) <= (r * r);
    }

}
