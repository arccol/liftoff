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

    public Trajectory(Vector2 vel, Vector2 pos, List<RealPlanet> planets, ShapeRenderer sr, Vector2 mouseForce) {
        this.vel = vel.cpy();
        this.pos = pos.cpy();
        this.sr = sr;
        this.planets = planets;
        this.mouseForce = mouseForce;
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
                Vector2 planetForce = planet.getPosition().cpy()
                    .sub(pos)
                    .setLength(planet.getDens() * planet.getSize() / 200f);

                vel.add(planetForce);
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

    public void setLength(int leng){
        this.leng = leng;
    }
}
