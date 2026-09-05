package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Trajectory {
    Vector2 pos;
    Vector2 vel;
    List<RealPlanet> planets;
    ShapeRenderer sr;
    Vector2[] markers;
    Vector2 mouseForce;
    int leng;
    int rad;

    public Trajectory(Vector2 vel, Vector2 pos, List<RealPlanet> planets, ShapeRenderer sr, Vector2 mouseForce, int rad) {
        this.vel = vel.cpy();
        this.pos = pos.cpy();
        this.sr = sr;
        this.planets = planets;
        this.mouseForce = mouseForce;
        this.rad = rad;
        leng = 5000;
        markers = new Vector2[leng];
    }

    public void generate() {
        vel.add(mouseForce);
        for(int i=0;i<markers.length;i++) {
            markers[i] = new Vector2();
            for (RealPlanet planet : planets) {
                if (planet == null) {
                    continue;
                }
                if(checkColPlanet(planet)) {
                    moveCol(planet);
                }
                Vector2 planetDir = planet.getPosition().cpy()
                    .sub(pos);

                float distance = planetDir.len();
                float gravityRadius = planet.getSize() * 100f;

                if(distance < gravityRadius && distance > 1) {
                    planetDir.nor();
                    float strength = (planet.getDens() * planet.getSize() * 20)
                        / (distance * distance);

                    strength = Math.min(strength, 5f);

                    vel.add(planetDir.scl(strength));
                }
            }

            pos.add(vel);
            markers[i].set(pos.cpy());
        }
        dropmarkers(markers);
    }

    public void dropmarkers(Vector2[] marks) {
        for(int i = 0; i < marks.length; i++) {
            if (2f / (i/300f + 1) > 0.3) {
                sr.setColor(Color.WHITE);
                sr.circle(marks[i].x, marks[i].y, 2f / (i / 300f + 1));
            }
        }
    }

    public boolean checkColPlanet(RealPlanet target){
        Vector2 targetPos;
        targetPos = target.position;
        float difference;
        difference = pos.dst(targetPos);
        if(difference>rad+target.rad){
            return false;
        }else{
            return true;
        }
    }

    public void moveCol(RealPlanet target) {

        Vector2 delta = new Vector2(pos).sub(target.position);
        float dist = delta.len();
        if(dist == 0) return;

        float overlap = (rad + target.rad) - dist;

        if(overlap > 0) {
            delta.nor();
            pos.add(delta.cpy().scl(overlap));

            float velocityIntoSurface = vel.dot(delta);

            if(velocityIntoSurface < 0) {
                vel.sub(delta.cpy().scl(velocityIntoSurface));
            }

            vel.scl(0.9f);

            vel.add(delta.cpy().scl(0.1f));
        }
    }

    public void setLength(int leng){
        this.leng = leng;
    }
}
