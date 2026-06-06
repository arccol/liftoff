package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class Perlin {
    Pixmap pixmap;
    SpriteBatch spriteBatch;
    Texture noiseTexture;
    int octaves = 10;
    int width = 1280;
    int height = 960;
    Vector2[] gradients = {
        new Vector2(1, 0),
        new Vector2(-1, 0),
        new Vector2(0, 1),
        new Vector2(0, -1),
        new Vector2(1, 1),
        new Vector2(-1, 1),
        new Vector2(1, -1),
        new Vector2(-1, -1)
    };
    int[] permutationtable = new int[512];

    public Perlin() {
        pixmap = new Pixmap(width, height, Pixmap.Format.RGB888);
    }

    public void updatePixmap() {
        createpermtable();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float brightness = FBM(x, y, octaves);
                pixmap.setColor(brightness, brightness, brightness, 1);
                pixmap.drawPixel(x, y);
            }
        }
    }

    public void createpermtable() {
        Random random = new Random();
        int[] baseValues = new int[256];

        for (int i = 0; i < 256; i++) {
            baseValues[i] = i;
        }


        for (int i = 255; i > 0; i--) {
            int ri = random.nextInt(i + 1);

            int temp = baseValues[i];
            baseValues[i] = baseValues[ri]; // swap randomindex with current index
            baseValues[ri] = temp;
        }

        for (int i = 0; i < 512; i++) {
            permutationtable[i] = baseValues[i % 256];
        }
    }

    public float generate(float x, float y) {

        int gridX = (int) Math.floor(x) & 255;
        int gridY = (int) Math.floor(y) & 255;

        float localX = x - (float) Math.floor(x);
        float localY = y - (float) Math.floor(y);

        float fadeX = fade(localX);
        float fadeY = fade(localY);

        int tl = permutationtable[permutationtable[gridX] + gridY];
        int tr = permutationtable[permutationtable[gridX + 1] + gridY];
        int bl = permutationtable[permutationtable[gridX] + gridY + 1];
        int br = permutationtable[permutationtable[gridX + 1] + gridY + 1];

        float tlG = gradient(tl, localX, localY);
        float trG = gradient(tr, localX - 1, localY);
        float blG = gradient(bl, localX, localY - 1);
        float brG = gradient(br, localX - 1, localY - 1);

        float interpolatedTop = interpolate(tlG, trG, fadeX);
        float interpolatedBottom = interpolate(blG, brG, fadeX);

        float finalValue = interpolate(interpolatedTop, interpolatedBottom, fadeY);

        return (finalValue + 1f) / 2f;
    }

    public float fade(float value) {
        return value * value * value * (value * (value * 6 - 15) + 10); // smooth curve
    }

    public float interpolate(float start, float end, float t) {
        return start + t * (end - start); // linear interpolation
    }

    public float gradient(int num, float xOffset, float yOffset) {
        Vector2 gradient = gradients[num % gradients.length];
        Vector2 distance = new Vector2(xOffset, yOffset);

        return gradient.dot(distance);
    }

    public float FBM(int x, int y, int numOctaves) {
        float brightness = 0f;
        float amp = 1f;
        float freq = 0.005f;

        for (int octave = 0; octave < numOctaves; octave++) {
            float n = amp * generate(x * freq, y * freq);
            brightness += n;
            amp *= 0.5f;
            freq *= 2;
        }
        return brightness;
    }
}
