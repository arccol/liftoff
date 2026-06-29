package io.github.some_example_name;

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
        leng = 500;
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
                Vector2 planetForce = planet.getPosition().cpy()
                    .sub(pos)
                    .scl(planet.getDens() * planet.getSize() / 20000f);

                vel.add(planetForce.scl((float) (1/Math.sqrt(planetForce.len()))));
            }
            pos.add(vel);
            markers[i].set(pos.cpy());
        }
        dropmarkers(markers);
    }

    public void dropmarkers(Vector2[] marks){
        for(Vector2 mark : marks){
            sr.circle(mark.x,mark.y,1f);
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

        float overlap = (rad + target.rad) - dist;

        if (overlap > 0) {
            delta.nor();

            pos.add(delta.cpy().scl(overlap*rad/50));

            float vector1 = vel.dot(delta);

            vel.sub(delta.cpy().scl(vector1));

            vel.scl(0.9f);
        }
    }

    public void setLength(int leng){
        this.leng = leng;
    }
}
