package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class InvenPlanet {
    float x;
    float y;
    int rad;
    int dens;
    Vector2 position = new Vector2();
    Vector2 vel = new Vector2();
    Vector2 grav = new Vector2();
    Texture planetTexture;

    public InvenPlanet(int x, int y, int r, int dens, Texture planetTexture){
        this.x = x;
        this.y = y;
        this.rad = r;
        this.dens = dens;
        this.planetTexture = planetTexture;
        position.set(x,y);
        vel.set(0,0);
        grav.set(0,-0.1f);
    }

    public int getDens(){
        return dens;
    }

    public int getSize(){
        return rad;
    }

    public void applyGrav(){
        vel.add(grav);
    }

    public boolean checkLost(){
        if(position.y<-rad){
            return true;
        }else{
            return false;
        }
    }

    public void updatePos(){
        position.add(vel);
        vel.scl(0.99f);
    }

    public void draw(ShapeRenderer sr, boolean selected, SpriteBatch batch){

        batch.begin();
        batch.setColor(1f-(float) dens/5, 1f-(float) dens/5, 1f-(float) dens/5, 1f);
        batch.draw(planetTexture,position.x- rad,position.y- rad, rad *2, rad *2);
        batch.end();

        if(selected){
            sr.setColor(Color.GOLD);
            sr.circle(position.x, position.y, rad + 4);
        }
        float shade = 1 - ((float) dens / 5);
        sr.setColor(new Color(shade, shade, shade, 1));
        sr.circle(position.x,position.y, rad);
        sr.setColor(Color.WHITE);
    }

    public boolean checkColPlanet(InvenPlanet target){
        Vector2 targetPos;
        targetPos = target.position;
        float difference;
        difference = position.dst(targetPos);
        if(difference> rad +target.rad){
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

        float overlap = (rad + target.rad) - dist;

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

        if(dist + rad <= potRadius){
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
            potCenter.x + delta.x * (potRadius - rad),
            potCenter.y + delta.y * (potRadius - rad)
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

        return (dx*dx+dy*dy) < (rad * rad);
    }

    public void hitBox(Box box) {
        float closestX = Math.max(box.x, Math.min(position.x, box.x + box.w));
        float closestY = Math.max(box.y, Math.min(position.y, box.y + box.h));

        float dx = position.x - closestX;
        float dy = position.y - closestY;

        float overlapX = rad - Math.abs(dx);
        float overlapY = rad - Math.abs(dy);

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
        return (dx * dx + dy * dy) <= (rad * rad);
    }

}
