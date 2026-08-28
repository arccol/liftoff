package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.Vector;

public class InvenPlanet {
    float x;
    float y;
    int r;
    int dens;
    Vector2 position = new Vector2();
    Vector2 vel = new Vector2();
    Vector2 grav = new Vector2();

    public InvenPlanet(int x, int y, int r){
        this(x, y, r, 1);
    }

    public InvenPlanet(int x, int y, int r, int dens){
        this.x = x;
        this.y = y;
        this.r = r;
        this.dens = dens;
        position.set(x,y);
        vel.set(0,0);
        grav.set(0,-0.1f);
    }

    public int getDens(){
        return dens;
    }

    public int getSize(){
        return r;
    }

    public void applyGrav(){
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
        draw(sr, false);
    }

    public void draw(ShapeRenderer sr, boolean selected){
        if(selected){
            sr.setColor(Color.GOLD);
            sr.circle(position.x, position.y, r + 4);
        }
        float shade = 1 - ((float) dens / 5);
        sr.setColor(new Color(shade, shade, shade, 1));
        sr.circle(position.x,position.y,r);
        sr.setColor(Color.WHITE);
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

        if (dist < 0.0001f) {
            delta.set(1, 0);
            dist = 0.0001f;
        }

        float overlap = (r + target.r) - dist;

        if (overlap > 0) {
            delta.nor();

            Vector2 correction = delta.cpy().scl(overlap * 0.5f);
            position.add(correction);
            target.position.sub(correction);

            Vector2 relativeVel = new Vector2(vel).sub(target.vel);
            float approachSpeed = relativeVel.dot(delta);

            if (approachSpeed < 0) {
                Vector2 impulse = delta.cpy().scl(approachSpeed * 0.5f);
                vel.sub(impulse);
                target.vel.add(impulse);
            }
        }
    }

    public void constrainToPot(Vector2 potCenter, float potRadius, float openingHalfWidthDeg){
        Vector2 delta = new Vector2(position).sub(potCenter);
        float dist = delta.len();

        if(dist + r <= potRadius){
            return;
        }

        if(dist < 0.0001f){
            delta.set(0,1);
            dist = 0.0001f;
        }

        float angleDeg = (float) Math.toDegrees(Math.atan2(delta.y, delta.x));
        float diffFromTop = Math.abs(angleDeg - 90f);
        if(diffFromTop < openingHalfWidthDeg){
            return;
        }

        delta.nor();

        position.set(
            potCenter.x + delta.x * (potRadius - r),
            potCenter.y + delta.y * (potRadius - r)
        );

        float outward = vel.dot(delta);
        if(outward > 0){
            vel.sub(delta.cpy().scl(outward));
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
