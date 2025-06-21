package main;

import Characters.*;
import Multiplayer.DatosJuego;
import Multiplayer.Mensaje;
import Multiplayer.PosicionJugador;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

/** Super Mario Brothers-like very basic platformer, using a tile map built using <a href="https://www.mapeditor.org/">Tiled</a> and a
 * tileset and sprites by <a href="http://www.vickiwenderlich.com/">Vicky Wenderlich</a></p>
 *
 * Shows simple platformer collision detection as well as on-the-fly map modifications through destructible blocks!
 * @author mzechner */
public class Main_Server extends InputAdapter implements ApplicationListener {

    public static DatosJuego datosJuego;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera pantalla;
    private Template personaje;
    private final Array<Rectangle> tiles = new Array<Rectangle>();
    private static final float GRAVITY = -2.5f;
    private boolean debug = false;
    private ShapeRenderer debugRenderer;
    private Server server = null;
    private Client client = null;
    private final Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject () {
            return new Rectangle();
        }
    };

    // Constructor para el servidor
    public Main_Server(Server server) {
        this.server = server;
        registrarClasesKryo();
    }

    // se podria pasar a generico
    private void registrarClasesKryo() {
        server.getKryo().register(Sonic.class);
        //server.getKryo().register(Koala.class);
        server.getKryo().register(Tails.class);
        server.getKryo().register(Template.class);
        server.getKryo().register(Mensaje.class);
        server.getKryo().register(com.badlogic.gdx.graphics.g2d.Animation.class);
        server.getKryo().register(com.badlogic.gdx.graphics.g2d.TextureRegion[].class);
        server.getKryo().register(com.badlogic.gdx.graphics.g2d.TextureRegion[].class);
        server.getKryo().register(com.badlogic.gdx.graphics.Texture.class);
        server.getKryo().register(com.badlogic.gdx.graphics.glutils.FileTextureData.class);
        //server.getKryo().register(PosicionJugador.class);
    }

    @Override
    public void create() {
        // Si no hay cliente, se asume que es un servidor
        personaje = new Sonic();
        personaje.create();

        // load the map, set the unit scale to 1/16 (1 unit == 16 pixels)
        map = new TmxMapLoader().load("Niveles/level3.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);
        // create an orthographic camera, shows us 30x20 units of the world
        pantalla = new OrthographicCamera();
        pantalla.setToOrtho(false, 30, 20);
        pantalla.update();

        personaje.position.set(0, 20);
        debugRenderer = new ShapeRenderer();
    }

    @Override
    public void render () {
        // colorea la pantalla de azul claro
        ScreenUtils.clear(0.7f, 0.7f, 1.0f, 1);
        // get the delta time
        float deltaTime = Gdx.graphics.getDeltaTime();
        // update the sonic (process input, collision detection, position update)
        updateCharacter(personaje, deltaTime);
        // if we have a server, send the position of the sonic to all clients\

        //PosicionJugador pos = new PosicionJugador();
        //pos.x = personaje.position.x;
        //pos.y = personaje.position.y;
        //server.sendToAllTCP(pos);

        // let the camera follow the sonic, x-axis only
        pantalla.position.x = personaje.position.x;
        pantalla.update();
        // set the TiledMapRenderer view based on what the
        // pantalla sees, and render the map
        renderer.setView(pantalla);
        renderer.render();
        // render the sonic
        personaje.render(deltaTime, renderer.getBatch());
        // render debug rectangles
        if (debug) renderDebug();
    }

    // Actualiza al personaje según la entrada del usuario y la fisica del juego
    private void updateCharacter(Template player, float deltaTime) {
        if (deltaTime == 0) return;

        if (deltaTime > 0.1f)
            deltaTime = 0.1f;

        player.stateTime += deltaTime;
        // detecta las entradas del usuario
        if ((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.5f, 1)) && player.isGrounded()) { player.jump();}
        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A) || isTouched(0, 0.25f)) { player.moveLeft();}
        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D) || isTouched(0.25f, 0.5f)) { player.moveRight();}
        if (Gdx.input.isKeyJustPressed(Keys.B)) debug = !debug;
        // apply gravity if we are falling
        player.velocity.add(0, GRAVITY);
        // clamp the velocity to the maximum, x-axis only
        player.velocity.x = MathUtils.clamp(player.velocity.x,
                -player.getMaxVelocity(), player.getMaxVelocity());
        // If the velocity is < 1, set it to 0 and set state to Standing
        if (Math.abs(player.velocity.x) < 1) {
            player.stand();
        }
        // multiply by delta time so we know how far we go
        // in this frame
        player.velocity.scl(deltaTime);

        // perform collision detection & response, on each axis, separately
        // if the sonic is moving right, check the tiles to the right of it's
        // right bounding box edge, otherwise check the ones to the left
        Rectangle playerRect = rectPool.obtain();
        playerRect.set(player.position.x, player.position.y, player.getWidth(), player.getHeight());
        int startX, startY, endX, endY;
        if (player.velocity.x > 0) {
            startX = endX = (int)(player.position.x + player.getWidth() + player.velocity.x);
        } else {
            startX = endX = (int)(player.position.x + player.velocity.x);
        }
        startY = (int)(player.position.y);
        endY = (int)(player.position.y + player.getHeight());
        getTiles(startX, startY, endX, endY, tiles);
        playerRect.x += player.velocity.x;
        for (Rectangle tile : tiles) {
            if (playerRect.overlaps(tile)) {
                player.velocity.x = 0;
                break;
            }
        }
        playerRect.x = player.position.x;

        // if the sonic is moving upwards, check the tiles to the top of its
        // top bounding box edge, otherwise check the ones to the bottom
        if (player.velocity.y > 0) {
            startY = endY = (int)(player.position.y + player.getHeight() + player.velocity.y);
        } else {
            startY = endY = (int)(player.position.y + player.velocity.y);
        }
        startX = (int)(player.position.x);
        endX = (int)(player.position.x + player.getWidth());
        getTiles(startX, startY, endX, endY, tiles);
        playerRect.y += player.velocity.y;
        for (Rectangle tile : tiles) {
            if (playerRect.overlaps(tile)) {
                // we actually reset the sonic y-position here
                // so it is just below/above the tile we collided with
                // this removes bouncing :)
                if (player.velocity.y > 0) {
                    player.position.y = tile.y - player.getHeight();
                    // we hit a block jumping upwards, let's destroy it!
                    TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("walls");
                    layer.setCell((int)tile.x, (int)tile.y, null);
                } else {
                    player.resetJump(tile.y, tile.height);
                }
                player.velocity.y = 0;
                break;
            }
        }
        rectPool.free(playerRect);

        // unscale the velocity by the inverse delta time and set
        // the latest position
        player.position.add(player.velocity);
        player.velocity.scl(1 / deltaTime);

        // Apply damping to the velocity on the x-axis so we don't
        // walk infinitely once a key was pressed
        player.velocity.x *= player.getDamping();
    }

    private boolean isTouched (float startX, float endX) {
        // Check for touch inputs between startX and endX
        // startX/endX are given between 0 (left edge of the screen) and 1 (right edge of the screen)
        for (int i = 0; i < 2; i++) {
            float x = Gdx.input.getX(i) / (float)Gdx.graphics.getBackBufferWidth();
            if (Gdx.input.isTouched(i) && (x >= startX && x <= endX)) {
                return true;
            }
        }
        return false;
    }

    private void getTiles (int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("walls");
        rectPool.freeAll(tiles);
        tiles.clear();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    tiles.add(rect);
                }
            }
        }
    }

    private void renderDebug () {
        debugRenderer.setProjectionMatrix(pantalla.combined);
        debugRenderer.begin(ShapeType.Line);

        debugRenderer.setColor(Color.RED);
        debugRenderer.rect(personaje.position.x, personaje.position.y, personaje.getWidth(), personaje.getHeight());

        debugRenderer.setColor(Color.YELLOW);
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("walls");
        for (int y = 0; y <= layer.getHeight(); y++) {
            for (int x = 0; x <= layer.getWidth(); x++) {
                Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    if (pantalla.frustum.boundsInFrustum(x + 0.5f, y + 0.5f, 0, 1, 1, 0))
                        debugRenderer.rect(x, y, 1, 1);
                }
            }
        }
        debugRenderer.end();
    }

    @Override
    public void dispose () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
    }
}
